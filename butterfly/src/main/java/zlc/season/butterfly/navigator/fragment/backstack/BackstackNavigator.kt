package zlc.season.butterfly.navigator.fragment.backstack

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.entities.DestinationData

interface BackstackNavigator {
    suspend fun navigate(
        activity: FragmentActivity,
        destinationData: DestinationData
    ): Fragment
}