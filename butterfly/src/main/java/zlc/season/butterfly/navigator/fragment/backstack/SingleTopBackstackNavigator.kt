package zlc.season.butterfly.navigator.fragment.backstack

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData

class SingleTopBackstackNavigator(
    val backStackEntryManager: BackStackEntryManager
) : BackstackNavigator {
    private val backstackNavigatorHelper = BackstackNavigatorHelper()

    override suspend fun navigate(
        activity: FragmentActivity,
        destinationData: DestinationData
    ): Fragment {
        val topEntry = backStackEntryManager.getTopEntry(activity)

        // If topEntry is not null and the target Destination is the same as the topEntry,
        // show the topEntry's fragment and update its arguments.
        return if (
            topEntry != null &&
            topEntry.destinationData.className == destinationData.className
        ) {
            backstackNavigatorHelper.showFragmentAndUpdateArguments(
                activity,
                topEntry.destinationData,
                destinationData
            )
        } else {
            // Create and show a new Fragment.
            val fragment = backstackNavigatorHelper.createAndShowFragment(
                activity,
                destinationData
            )

            // Add fragment to back stack.
            if (destinationData.enableBackStack) {
                backStackEntryManager.addEntry(activity, BackStackEntry(destinationData))
            }

            fragment
        }
    }
}