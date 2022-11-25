package zlc.season.butterfly.internal

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import zlc.season.claritypotion.ClarityPotion

object ButterflyHelper {
    internal const val AGILE_REQUEST = "butterfly_request"

    private val internalScope by lazy { MainScope() }

    internal val application: Application
        get() = ClarityPotion.application

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


    fun Activity.setActivityResult(bundle: Bundle) {
        if (bundle.isEmpty) return
        setResult(RESULT_OK, Intent().apply { putExtras(bundle) })
    }

    fun Activity.contentView(): ViewGroup {
        return findViewById(android.R.id.content)
    }
}