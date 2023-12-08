package zlc.season.butterfly.navigator.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.internal.ButterflyFragment.Companion.awaitFragmentResult
import zlc.season.butterfly.navigator.Navigator

class DialogFragmentNavigator(val backStackEntryManager: BackStackEntryManager) : Navigator {

    override suspend fun navigate(context: Context, data: DestinationData): Result<Bundle> {
        return if (context is FragmentActivity) {
            navigate(context, data)
        } else {
            Result.success(Bundle.EMPTY)
        }
    }

    private suspend fun navigate(
        activity: FragmentActivity,
        destinationData: DestinationData
    ): Result<Bundle> {
        if (destinationData.enableBackStack) {
            backStackEntryManager.addEntry(activity, BackStackEntry(destinationData))
        }

        activity.showDialogFragment(destinationData)

        return if (destinationData.needResult) {
            activity.awaitFragmentResult(destinationData.route, destinationData.uniqueTag)
        } else {
            Result.success(Bundle.EMPTY)
        }
    }

    override fun popBack(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {
        if (activity !is FragmentActivity) return
        with(activity) {
            val find = findDialogFragment(topEntry.destinationData) ?: return
            if (topEntry.destinationData.needResult) {
                setFragmentResult(topEntry.destinationData.uniqueTag, bundle)
            }
            find.dismissAllowingStateLoss()
        }
    }
}


