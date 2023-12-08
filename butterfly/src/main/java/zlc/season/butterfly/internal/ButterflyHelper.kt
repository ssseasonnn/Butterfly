package zlc.season.butterfly.internal

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import zlc.season.butterfly.entities.DestinationData
import zlc.season.claritypotion.ClarityPotion

object ButterflyHelper {
    internal const val KEY_DESTINATION_DATA = "key_butterfly_destination_data"

    val context: Context
        get() = activity ?: application

    val application: Application
        get() = ClarityPotion.application

    val activity: Activity?
        get() = ClarityPotion.activity

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

    @Suppress("deprecation")
    fun Activity.getDestinationData(): DestinationData? {
        return intent.getParcelableExtra(KEY_DESTINATION_DATA)
    }
}