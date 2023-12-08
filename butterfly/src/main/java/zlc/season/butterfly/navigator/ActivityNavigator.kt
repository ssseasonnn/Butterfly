package zlc.season.butterfly.navigator

import android.app.Activity
import android.app.Application
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
import kotlinx.coroutines.suspendCancellableCoroutine
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.internal.ButterflyFragment.Companion.startActivityAndAwaitResult
import zlc.season.butterfly.internal.ButterflyHelper.KEY_DESTINATION_DATA
import zlc.season.butterfly.internal.ButterflyHelper.getDestinationData
import zlc.season.butterfly.internal.ButterflyHelper.setActivityResult
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter

class ActivityNavigator(val backStackEntryManager: BackStackEntryManager) : Navigator {

    override suspend fun navigate(context: Context, data: DestinationData): Result<Bundle> {
        if (context is FragmentActivity) {
            return navigate(context, data)
        }

        val intent = createIntent(context, data)
        context.startActivity(intent, createActivityOptions(context, data)?.toBundle())
        context.awaitActivityCreated(data)
        return Result.success(Bundle.EMPTY)
    }

    private suspend fun navigate(
        activity: FragmentActivity,
        data: DestinationData
    ): Result<Bundle> {
        return if (!data.needResult) {
            val intent = createIntent(activity, data)
            activity.startActivity(intent, createActivityOptions(activity, data)?.toBundle())
            activity.awaitActivityCreated(data)
            Result.success(Bundle.EMPTY)
        } else {
            val intent = createIntent(activity, data)
            createActivityOptions(activity, data)?.toBundle()?.let {
                intent.putExtra(EXTRA_ACTIVITY_OPTIONS_BUNDLE, it)
            }
            activity.startActivityAndAwaitResult(data.scheme, intent)
        }
    }

    override fun popBack(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {
        with(activity) {
            if (topEntry.destinationData.needResult) {
                setActivityResult(bundle)
            }
            finish()
        }
    }

    private fun createIntent(context: Context, data: DestinationData): Intent {
        val intent = Intent().apply {
            putExtra(KEY_DESTINATION_DATA, data)
            setClassName(context.packageName, data.className)
            putExtras(data.bundle)

            if (data.clearTop) {
                addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            } else if (data.singleTop) {
                addFlags(FLAG_ACTIVITY_SINGLE_TOP)
            }
            if (data.flags != 0) {
                addFlags(data.flags)
            }
            if (context !is Activity) {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
        }

        return intent
    }

    private fun createActivityOptions(
        context: Context, data: DestinationData
    ): ActivityOptionsCompat? {
        return if (data.enterAnim != 0 || data.exitAnim != 0) {
            makeCustomAnimation(context, data.enterAnim, data.exitAnim)
        } else {
            null
        }
    }

    private suspend fun Context.awaitActivityCreated(data: DestinationData) =
        suspendCancellableCoroutine {
            val application = this.applicationContext as Application
            val callback = object : ActivityLifecycleCallbacksAdapter() {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    val destinationData = activity.getDestinationData()
                    if (destinationData?.uniqueTag == data.uniqueTag) {
                        application.unregisterActivityLifecycleCallbacks(this)
                        if (it.isActive) {
                            it.resumeWith(Result.success(Unit))
                        }
                    }
                }
            }

            application.registerActivityLifecycleCallbacks(callback)

            it.invokeOnCancellation {
                application.unregisterActivityLifecycleCallbacks(callback)
            }
        }

}