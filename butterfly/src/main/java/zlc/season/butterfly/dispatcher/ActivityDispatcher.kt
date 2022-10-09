package zlc.season.butterfly.dispatcher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.*
import zlc.season.butterfly.ButterflyHelper.setActivityResult

object ActivityDispatcher : InnerDispatcher {
    override fun retreatCount(): Int {
        return ButterflyHelper.activitySize
    }

    override fun retreat(bundle: Bundle): Boolean {
        ButterflyHelper.activity?.let {
            it.setActivityResult(bundle)
            it.finish()
            return true
        }
        return false
    }

    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        return if (!request.needResult) {
            val context = ButterflyHelper.context
            val intent = createIntent(context, request)
            context.startActivity(intent, createActivityOptions(context, request)?.toBundle())

            flowOf(Result.success(Bundle()))
        } else {
            val activity = ButterflyHelper.fragmentActivity
            if (activity != null) {
                val intent = createIntent(activity, request)
                ButterflyFragment.showAsFlow(activity, intent, createActivityOptions(activity, request))
            } else {
                "Agile --> activity not found".logw()
                flowOf(Result.failure(IllegalStateException("Activity not found")))
            }
        }
    }

    private fun createIntent(context: Context, request: AgileRequest): Intent {
        val intent = Intent()
        intent.putExtra(Butterfly.RAW_SCHEME, request.scheme)
        intent.setClassName(context.packageName, request.className)
        intent.putExtras(request.bundle)

        if (request.activityConfig.clearTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else if (request.activityConfig.singleTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        if (request.activityConfig.flags != 0) {
            intent.addFlags(request.activityConfig.flags)
        }
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }

    private fun createActivityOptions(
        context: Context, request: AgileRequest
    ): ActivityOptionsCompat? {
        val config = request.activityConfig
        return config.activityOptions ?: if (config.enterAnim != 0 || config.exitAnim != 0) {
            ActivityOptionsCompat.makeCustomAnimation(
                context, config.enterAnim, config.exitAnim
            )
        } else {
            null
        }
    }
}