package zlc.season.butterfly.internal

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import zlc.season.butterfly.AgileRequest

internal fun FragmentActivity.createFragment(request: AgileRequest): Fragment {
    val fragment = supportFragmentManager.fragmentFactory.instantiate(
        classLoader, request.className
    )
    fragment.arguments = request.bundle
    return fragment
}

internal fun FragmentActivity.showNewFragment(request: AgileRequest): Fragment {
    val fragment = createFragment(request)
    with(supportFragmentManager.beginTransaction()) {
        setCustomAnimations(request.enterAnim, request.exitAnim, 0, 0)
        if (request.useReplace) {
            replace(request.containerId(), fragment, request.uniqueId)
        } else {
            add(request.containerId(), fragment, request.uniqueId)
        }

        commitAllowingStateLoss()
    }
    return fragment
}

private fun AgileRequest.containerId(): Int {
    return if (containerViewId != 0) containerViewId else android.R.id.content
}

internal fun FragmentActivity.createDialogFragment(request: AgileRequest): DialogFragment {
    return createFragment(request) as DialogFragment
}

internal fun FragmentActivity.showDialogFragment(request: AgileRequest): DialogFragment {
    val dialogFragment = createDialogFragment(request)
    dialogFragment.show(supportFragmentManager, request.uniqueId)
    return dialogFragment
}

internal fun FragmentActivity.findFragment(request: AgileRequest): Fragment? {
    return supportFragmentManager.findFragmentByTag(request.uniqueId)
}

internal fun FragmentActivity.findDialogFragment(request: AgileRequest): DialogFragment? {
    return supportFragmentManager.findFragmentByTag(request.uniqueId) as? DialogFragment
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

internal fun FragmentActivity.awaitFragmentResume(
    fragment: Fragment,
    callback: ProducerScope<Result<Bundle>>.() -> Unit
) = callbackFlow {
    val cb = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            if (fragment === f) {
                callback()
                supportFragmentManager.unregisterFragmentLifecycleCallbacks(this)
            }
        }
    }

    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            close()
            supportFragmentManager.unregisterFragmentLifecycleCallbacks(cb)
            lifecycle.removeObserver(this)
        }
    })

    if (!isDestroyed && isActive) {
        supportFragmentManager.registerFragmentLifecycleCallbacks(cb, false)
        addFragment(fragment)
    }

    awaitClose {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(cb)
    }
}

internal fun FragmentActivity.awaitFragmentResult(fragment: Fragment, requestKey: String) = callbackFlow {
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
