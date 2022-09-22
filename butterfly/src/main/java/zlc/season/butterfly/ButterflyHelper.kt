package zlc.season.butterfly

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import zlc.season.claritypotion.ClarityPotion

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
}