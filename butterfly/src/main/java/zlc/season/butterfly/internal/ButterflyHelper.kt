package zlc.season.butterfly.internal

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import zlc.season.claritypotion.ClarityPotion

object ButterflyHelper {
    internal const val AGILE_REQUEST = "butterfly_request"

    private val internalScope by lazy { MainScope() }

    val application: Application
        get() = ClarityPotion.application

    val activity: Activity?
        get() = ClarityPotion.activity

    val componentActivity: ComponentActivity?
        get() = with(activity) {
            if (this != null && this is ComponentActivity) {
                this
            } else {
                null
            }
        }

    fun Activity.setActivityResult(bundle: Bundle) {
        if (bundle.isEmpty) return
        setResult(RESULT_OK, Intent().apply { putExtras(bundle) })
    }

    fun Activity.contentView(): ViewGroup {
        return findViewById(android.R.id.content)
    }

    fun Context.findActivity(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }

    fun Context.findComponentActivity(): ComponentActivity? {
        val activity = findActivity()
        if (activity is ComponentActivity) {
            return activity
        }
        return null
    }

    fun Context.findScope(): CoroutineScope {
        val fragmentActivity = findComponentActivity()
        return fragmentActivity?.lifecycleScope ?: internalScope
    }
}