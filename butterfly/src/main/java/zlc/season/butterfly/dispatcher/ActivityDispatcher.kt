package zlc.season.butterfly.dispatcher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult.Companion.EXTRA_ACTIVITY_OPTIONS_BUNDLE
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.ActivityOptionsCompat.makeCustomAnimation
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.internal.ButterflyFragment.Companion.awaitActivityResult
import zlc.season.butterfly.internal.ButterflyHelper.AGILE_REQUEST
import zlc.season.butterfly.internal.ButterflyHelper.setActivityResult

class ActivityDispatcher(val backStackEntryManager: BackStackEntryManager) : InnerDispatcher {

    override suspend fun dispatch(context: Context, request: AgileRequest): Flow<Result<Bundle>> {
        if (context is FragmentActivity) {
            return dispatch(context, request)
        }
        val intent = createIntent(context, request)
        context.startActivity(intent, createActivityOptions(context, request)?.toBundle())
        return emptyFlow()
    }

    private fun dispatch(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> {
        return if (!request.needResult) {
            val intent = createIntent(activity, request)
            activity.startActivity(intent, createActivityOptions(activity, request)?.toBundle())
            emptyFlow()
        } else {
            val intent = createIntent(activity, request)
            createActivityOptions(activity, request)?.toBundle()?.let {
                intent.putExtra(EXTRA_ACTIVITY_OPTIONS_BUNDLE, it)
            }
            activity.awaitActivityResult(request.scheme, intent)
        }
    }

    override fun retreat(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {
        with(activity) {
            if (topEntry.request.needResult) {
                setActivityResult(bundle)
            }
            finish()
        }
    }

    private fun createIntent(context: Context, request: AgileRequest): Intent {
        val intent = Intent().apply {
            putExtra(AGILE_REQUEST, request)
            setClassName(context.packageName, request.className)
            putExtras(request.bundle)

            if (request.clearTop) {
                addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            } else if (request.singleTop) {
                addFlags(FLAG_ACTIVITY_SINGLE_TOP)
            }
            if (request.flags != 0) {
                addFlags(request.flags)
            }
            if (context !is Activity) {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
        }

        return intent
    }

    private fun createActivityOptions(
        context: Context, request: AgileRequest
    ): ActivityOptionsCompat? {
        return if (request.enterAnim != 0 || request.exitAnim != 0) {
            makeCustomAnimation(context, request.enterAnim, request.exitAnim)
        } else {
            null
        }
    }
}