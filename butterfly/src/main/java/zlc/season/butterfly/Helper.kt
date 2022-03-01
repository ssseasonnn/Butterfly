package zlc.season.butterfly

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive

@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T : Fragment> FragmentManager.showAsFlow(fragment: T, callback: ProducerScope<Result<Intent>>.() -> Unit) = callbackFlow {
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
    }
}


internal fun FragmentManager.add(fragment: Fragment, tag: String) {
    beginTransaction().add(fragment, tag).commitAllowingStateLoss()
}

internal fun FragmentManager.remove(fragment: Fragment) {
    beginTransaction().remove(fragment).commitAllowingStateLoss()
}