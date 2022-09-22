@file:OptIn(ExperimentalCoroutinesApi::class)

package zlc.season.butterfly

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive


@OptIn(ExperimentalCoroutinesApi::class)
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

@OptIn(ExperimentalCoroutinesApi::class)
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


internal fun FragmentManager.add(fragment: Fragment) {
    beginTransaction().add(fragment, fragment.javaClass.name).commitAllowingStateLoss()
}

internal fun FragmentManager.remove(fragment: Fragment) {
    beginTransaction().remove(fragment).commitAllowingStateLoss()
}