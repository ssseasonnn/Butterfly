package zlc.season.butterfly.navigator.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import zlc.season.butterfly.entities.DestinationData

private fun FragmentActivity.createFragment(data: DestinationData): Fragment {
    val fragment = supportFragmentManager.fragmentFactory.instantiate(
        classLoader, data.className
    )
    fragment.arguments = data.bundle
    return fragment
}

internal fun FragmentActivity.createAndShowFragment(data: DestinationData): Fragment {
    val fragment = createFragment(data)
    with(supportFragmentManager.beginTransaction()) {
        setCustomAnimations(data.enterAnim, data.exitAnim, 0, 0)
        if (data.useReplace) {
            replace(findContainerViewId(data), fragment, data.uniqueTag)
        } else {
            add(findContainerViewId(data), fragment, data.uniqueTag)
        }

        commitAllowingStateLoss()
    }
    return fragment
}

/**
 * Finds the target container view id for a destination within a FragmentActivity.
 *
 * @param data Information about the destination, including potential container view id or tag.
 * @return The id of the container view to use, or [android.R.id.content] if no specific container is found.
 */
private fun FragmentActivity.findContainerViewId(data: DestinationData): Int {
    var result = 0
    if (data.containerViewId != 0) {
        result = data.containerViewId
    } else if (data.containerViewTag.isNotEmpty()) {
        val containerView = window.decorView.findViewWithTag<ViewGroup>(data.containerViewTag)
        if (containerView != null && containerView.id != View.NO_ID) {
            result = containerView.id
        }
    }

    return if (result != 0) result else android.R.id.content
}

internal fun FragmentActivity.createDialogFragment(request: DestinationData): DialogFragment {
    return createFragment(request) as DialogFragment
}

internal fun FragmentActivity.showDialogFragment(request: DestinationData): DialogFragment {
    val dialogFragment = createDialogFragment(request)
    dialogFragment.show(supportFragmentManager, request.uniqueTag)
    return dialogFragment
}

internal fun FragmentActivity.findFragment(data: DestinationData): Fragment? {
    return supportFragmentManager.findFragmentByTag(data.uniqueTag)
}

internal fun FragmentActivity.findDialogFragment(request: DestinationData): DialogFragment? {
    return supportFragmentManager.findFragmentByTag(request.uniqueTag) as? DialogFragment
}

internal fun FragmentActivity.addFragment(fragment: Fragment) {
    commit { add(fragment, fragment.javaClass.name) }
}

internal fun FragmentActivity.removeFragment(fragment: Fragment) {
    commit { remove(fragment) }
}

internal fun FragmentActivity.removeFragment(tag: String) {
    val find = supportFragmentManager.findFragmentByTag(tag)
    if (find != null) {
        commit { remove(find) }
    }
}

internal fun FragmentActivity.hideFragment(fragment: Fragment) {
    commit { hide(fragment) }
}

internal fun FragmentActivity.showFragment(fragment: Fragment) {
    commit { show(fragment) }
}

private fun FragmentActivity.commit(block: FragmentTransaction.() -> Unit) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.block()
    transaction.commitAllowingStateLoss()
}

internal fun FragmentActivity.observeFragmentDestroy(block: (Fragment) -> Unit) {
    val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            block(f)
        }
    }
    supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false)
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
            lifecycle.removeObserver(this)
        }
    })
}

suspend fun FragmentActivity.awaitFragmentResume(fragment: Fragment) =
    suspendCancellableCoroutine {
        if (fragment.isResumed) {
            it.resumeWith(Result.success(Unit))
        } else {
            val callback = object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    if (fragment === f) {
                        supportFragmentManager.unregisterFragmentLifecycleCallbacks(this)
                        if (it.isActive) {
                            it.resumeWith(Result.success(Unit))
                        }
                    }
                }
            }

            val lifecycleObserver = object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    supportFragmentManager.unregisterFragmentLifecycleCallbacks(callback)
                    lifecycle.removeObserver(this)
                }
            }

            lifecycle.addObserver(lifecycleObserver)
            supportFragmentManager.registerFragmentLifecycleCallbacks(callback, false)

            it.invokeOnCancellation {
                supportFragmentManager.unregisterFragmentLifecycleCallbacks(callback)
                lifecycle.removeObserver(lifecycleObserver)
            }
        }
    }

internal fun FragmentActivity.awaitFragmentResult(fragment: Fragment, requestKey: String) =
    callbackFlow {
        val listener = FragmentResultListener { key, result ->
            if (requestKey == key) {
                trySend(Result.success(result))
                close()
                supportFragmentManager.clearFragmentResultListener(requestKey)
            }
        }
        if (!isDestroyed && isActive) {
            supportFragmentManager.setFragmentResultListener(
                requestKey,
                fragment,
                listener
            )
        }
        awaitClose {
            supportFragmentManager.clearFragmentResultListener(requestKey)
        }
    }

internal fun FragmentActivity.setFragmentResult(requestKey: String, bundle: Bundle) {
    if (bundle.isEmpty) return
    supportFragmentManager.setFragmentResult(requestKey, bundle)
}
