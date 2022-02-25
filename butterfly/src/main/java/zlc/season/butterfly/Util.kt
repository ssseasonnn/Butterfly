package zlc.season.butterfly

import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive

internal fun <T> T.logd(tag: String = ""): T {
    val realTag = tag.ifEmpty { "Butterfly" }
    if (this is Throwable) {
        Log.d(realTag, this.message ?: "", this)
    } else {
        Log.d(realTag, this.toString())
    }
    return this
}

val EMPTY_LAMBDA: (Result<Intent>) -> Unit = {}

fun <T : Fragment> FragmentManager.show(fragment: T, callback: (T) -> Unit) = run {
    val cb = object : FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            if (this === f) {
                callback(fragment)
                remove(fragment)
                unregisterFragmentLifecycleCallbacks(this)
            }
        }
    }
    if (!isDestroyed) {
        registerFragmentLifecycleCallbacks(cb, false)
        val tag = fragment.hashCode().toString()
        add(fragment, tag)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T : Fragment> FragmentManager.showAsFlow(fragment: T, callback: ProducerScope<Nothing>.(T) -> Unit) = callbackFlow {
    val cb = object : FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            if (fragment === f) callback(fragment)
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            if (fragment === f) close()
        }
    }

    if (isActive) {
        registerFragmentLifecycleCallbacks(cb, false)
        val tag = fragment.hashCode().toString()
        add(fragment, tag)
    }
    awaitClose {
        remove(fragment)
        unregisterFragmentLifecycleCallbacks(cb)
    }
}

private fun FragmentManager.add(fragment: Fragment, tag: String) {
    beginTransaction().add(fragment, tag).commitAllowingStateLoss()
}

private fun FragmentManager.remove(fragment: Fragment) {
    beginTransaction().remove(fragment).commitAllowingStateLoss()
}