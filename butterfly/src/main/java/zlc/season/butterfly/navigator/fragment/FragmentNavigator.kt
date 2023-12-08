package zlc.season.butterfly.navigator.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.core.GroupEntryManager
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.internal.ButterflyFragment.Companion.awaitFragmentResult
import zlc.season.butterfly.internal.ButterflyHelper.setActivityResult
import zlc.season.butterfly.navigator.Navigator

class FragmentNavigator(
    private val backStackEntryManager: BackStackEntryManager,
    private val groupEntryManager: GroupEntryManager
) : Navigator {

    private val navigatorContext = NavigatorContext(backStackEntryManager, groupEntryManager)

    override suspend fun navigate(context: Context, data: DestinationData): Result<Bundle> {
        return if (context is FragmentActivity) {
            navigate(context, data)
        } else {
            Result.success(Bundle.EMPTY)
        }
    }

    private suspend fun navigate(
        activity: FragmentActivity,
        request: DestinationData
    ): Result<Bundle> {
        navigatorContext.navigate(activity, request)

        return if (request.needResult) {
            activity.awaitFragmentResult(request.route, request.uniqueTag)
        } else {
            Result.success(Bundle.EMPTY)
        }
    }

    override fun popBack(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {
        if (activity !is FragmentActivity) return
        activity.apply {
            findFragment(topEntry.destinationData)?.let {
                if (topEntry.destinationData.needResult) {
                    setFragmentResult(topEntry.destinationData.uniqueTag, bundle)
                }
                removeFragment(it)
            }
        }
    }
}