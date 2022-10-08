package zlc.season.butterfly

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import zlc.season.claritypotion.ClarityPotion

@OptIn(ExperimentalCoroutinesApi::class)
internal object ButterflyHelper {
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

    internal val fragmentManager: FragmentManager?
        get() = fragmentActivity?.supportFragmentManager

    internal val lifecycleOwner: LifecycleOwner?
        get() = with(activity) {
            if (this != null && this is LifecycleOwner) {
                this
            } else {
                null
            }
        }

    internal val scope: CoroutineScope
        get() = lifecycleOwner?.lifecycleScope ?: internalScope


    internal fun FragmentManager.awaitFragmentResume(
        fragment: Fragment,
        callback: ProducerScope<Result<Bundle>>.() -> Unit
    ) = callbackFlow {
        val cb = object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                if (fragment === f) {
                    callback()
                    unregisterFragmentLifecycleCallbacks(this)
                }
            }
        }

        if (!isDestroyed && isActive) {
            registerFragmentLifecycleCallbacks(cb, false)
            add(fragment)
        }

        awaitClose {
            unregisterFragmentLifecycleCallbacks(cb)
        }
    }

    internal fun FragmentActivity.awaitFragmentResult(fragment: Fragment) = callbackFlow {
        val requestKey = fragment.javaClass.name
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
                this@awaitFragmentResult,
                listener
            )
        }
        awaitClose {
            supportFragmentManager.clearFragmentResultListener(requestKey)
        }
    }


    internal fun FragmentManager.add(fragment: Fragment) {
        beginTransaction().add(fragment, fragment.javaClass.name).commitAllowingStateLoss()
    }

    internal fun FragmentManager.remove(fragment: Fragment) {
        beginTransaction().remove(fragment).commitAllowingStateLoss()
    }
}