package zlc.season.butterfly

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

internal fun <T : Fragment> FragmentManager.show(fragment: T, callback: (T) -> Unit) = run {
    val cb = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            if (fragment === f) {
                callback(fragment)
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

internal fun FragmentManager.add(fragment: Fragment, tag: String) {
    beginTransaction().add(fragment, tag).commitAllowingStateLoss()
}

internal fun FragmentManager.remove(fragment: Fragment) {
    beginTransaction().remove(fragment).commitAllowingStateLoss()
}