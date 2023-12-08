package zlc.season.butterfly.navigator.fragment.backstack

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.navigator.fragment.awaitFragmentResume
import zlc.season.butterfly.navigator.fragment.createAndShowFragment

class StandardBackstackNavigator(
    val backStackEntryManager: BackStackEntryManager
) : BackstackNavigator {
    override suspend fun navigate(
        activity: FragmentActivity,
        destinationData: DestinationData
    ): Fragment {
        val fragment = activity.createAndShowFragment(destinationData)
        activity.awaitFragmentResume(fragment)

        if (destinationData.enableBackStack) {
            backStackEntryManager.addEntry(activity, BackStackEntry(destinationData))
        }

        return fragment
    }
}