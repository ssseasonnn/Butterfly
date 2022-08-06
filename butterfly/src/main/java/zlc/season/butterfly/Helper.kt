@file:OptIn(ExperimentalCoroutinesApi::class)

package zlc.season.butterfly

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
import zlc.season.claritypotion.ClarityPotion.Companion.currentActivity

private val scope by lazy { MainScope() }

internal fun currentCtx() = currentActivity() ?: ClarityPotion.clarityPotion

internal fun currentScope(): CoroutineScope {
    val activity = currentActivity()
    return if (activity != null && activity is LifecycleOwner) {
        activity.lifecycleScope
    } else {
        scope
    }
}

internal fun currentLifecycle(): LifecycleOwner? {
    val activity = currentActivity()
    return if (activity != null && activity is LifecycleOwner) {
        activity
    } else {
        null
    }
}

internal fun currentFragmentActivity(): FragmentActivity? {
    val activity = currentActivity()
    return if (activity != null && activity is FragmentActivity) {
        activity
    } else {
        null
    }
}

internal fun currentFm(): FragmentManager? {
    val activity = currentActivity()
    return if (activity != null && activity is FragmentActivity) {
        activity.supportFragmentManager
    } else {
        null
    }
}

internal fun FragmentManager.awaitFragment(
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
        val tag = fragment.hashCode().toString()
        add(fragment, tag)
    }

    awaitClose {
        unregisterFragmentLifecycleCallbacks(cb)
    }
}

internal fun FragmentManager.awaitFragmentResult(
    lifecycleOwner: LifecycleOwner,
    fragment: Fragment
) = callbackFlow {
    val requestKey = fragment.javaClass.name
    val listener = FragmentResultListener { key, result ->
        if (requestKey == key) {
            trySend(Result.success(result))
            close()
            clearFragmentResultListener(requestKey)
        }
    }
    if (!isDestroyed && isActive) {
        setFragmentResultListener(
            requestKey,
            lifecycleOwner,
            listener
        )
    }
    awaitClose {
        clearFragmentResultListener(requestKey)
    }
}


internal fun FragmentManager.add(fragment: Fragment, tag: String) {
    beginTransaction().add(fragment, tag).commitAllowingStateLoss()
}

internal fun FragmentManager.remove(fragment: Fragment) {
    beginTransaction().remove(fragment).commitAllowingStateLoss()
}