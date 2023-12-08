package zlc.season.butterfly.navigator.fragment.backstack

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.navigator.fragment.removeFragment

class ClearTopBackstackNavigator(
    val backStackEntryManager: BackStackEntryManager
) : BackstackNavigator {
    private val backstackNavigatorHelper = BackstackNavigatorHelper()

    override suspend fun navigate(
        activity: FragmentActivity,
        destinationData: DestinationData
    ): Fragment {
        // Get the entry list above on the target Destination, including the target Destination.
        val topEntryList = backStackEntryManager.getTopEntryList(activity, destinationData)

        // If the list is empty, create and show a new Fragment.
        return if (topEntryList.isEmpty()) {
            val fragment = backstackNavigatorHelper.createAndShowFragment(
                activity,
                destinationData
            )

            // Add fragment to back stack.
            if (destinationData.enableBackStack) {
                backStackEntryManager.addEntry(activity, BackStackEntry(destinationData))
            }
            fragment
        } else {
            // Remove all the Fragment above the target Destination.
            val targetEntry = topEntryList.removeFirst()
            topEntryList.forEach {
                activity.removeFragment(it.destinationData.uniqueTag)
            }
            backStackEntryManager.removeEntryList(activity, topEntryList)

            // Show the target Fragment and update its arguments.
            backstackNavigatorHelper.showFragmentAndUpdateArguments(
                activity,
                targetEntry.destinationData,
                destinationData
            )
        }
    }
}