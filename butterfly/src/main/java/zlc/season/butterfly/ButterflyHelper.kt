package zlc.season.butterfly

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import zlc.season.claritypotion.ClarityPotion

object ButterflyHelper {
    private val internalScope by lazy { MainScope() }

    internal val context: Context
        get() = activity ?: ClarityPotion.context

    internal val activity: Activity?
        get() = ClarityPotion.activity

    internal val fragmentActivity: FragmentActivity?
        get() = with(activity) {
            if (this != null && this is FragmentActivity) {
                this
            } else {
                null
            }
        }

    private val lifecycleOwner: LifecycleOwner?
        get() = with(activity) {
            if (this != null && this is LifecycleOwner) {
                this
            } else {
                null
            }
        }

    internal val scope: CoroutineScope
        get() = lifecycleOwner?.lifecycleScope ?: internalScope


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

        if (!isDestroyed && isActive) {
            supportFragmentManager.registerFragmentLifecycleCallbacks(cb, false)
            add(fragment)
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

    internal fun FragmentActivity.createFragment(request: AgileRequest): Fragment {
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader, request.className
        )
        fragment.arguments = request.bundle
        return fragment
    }

    internal fun FragmentActivity.createDialogFragment(request: AgileRequest): DialogFragment {
        return createFragment(request) as DialogFragment
    }

    fun Activity.setActivityResult(bundle: Bundle) {
        if (bundle.isEmpty) return
        setResult(RESULT_OK, Intent().apply { putExtras(bundle) })
    }

    fun Activity.contentView(): ViewGroup {
        return findViewById(android.R.id.content)
    }

    internal fun FragmentActivity.findFragment(request: AgileRequest): Fragment? {
        return supportFragmentManager.findFragmentByTag(request.uniqueId)
    }

    internal fun FragmentActivity.findDialogFragment(request: AgileRequest): DialogFragment? {
        return supportFragmentManager.findFragmentByTag(request.uniqueId) as? DialogFragment
    }

    internal fun FragmentActivity.add(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(fragment, fragment.javaClass.name).commitAllowingStateLoss()
    }

    internal fun FragmentActivity.remove(fragment: Fragment) {
        supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
    }

    internal fun FragmentActivity.remove(tag: String) {
        val find = supportFragmentManager.findFragmentByTag(tag)
        if (find != null) {
            supportFragmentManager.beginTransaction().remove(find).commitAllowingStateLoss()
        }
    }

    internal fun FragmentActivity.hide(fragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss()
    }

    internal fun FragmentActivity.show(fragment: Fragment) {
        supportFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss()
    }
}