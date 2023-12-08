package zlc.season.butterfly.navigator.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.core.GroupEntryManager
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.navigator.fragment.backstack.ClearTopBackstackNavigator
import zlc.season.butterfly.navigator.fragment.backstack.SingleTopBackstackNavigator
import zlc.season.butterfly.navigator.fragment.backstack.StandardBackstackNavigator
import zlc.season.butterfly.navigator.fragment.group.GroupNavigator

class NavigatorContext(
    backStackEntryManager: BackStackEntryManager,
    groupEntryManager: GroupEntryManager
) {
    private val standardBackstackNavigator = StandardBackstackNavigator(backStackEntryManager)
    private val clearTopBackstackNavigator = ClearTopBackstackNavigator(backStackEntryManager)
    private val singleTopBackstackNavigator = SingleTopBackstackNavigator(backStackEntryManager)

    private val groupNavigator = GroupNavigator(groupEntryManager)

    suspend fun navigate(
        activity: FragmentActivity,
        request: DestinationData
    ): Fragment {
        return if (request.groupId.isNotEmpty()) {
            groupNavigate(activity, request)
        } else {
            backstackNavigate(activity, request)
        }
    }

    private suspend fun backstackNavigate(
        activity: FragmentActivity,
        request: DestinationData
    ): Fragment {
        return if (request.clearTop) {
            clearTopBackstackNavigator.navigate(activity, request)
        } else if (request.singleTop) {
            singleTopBackstackNavigator.navigate(activity, request)
        } else {
            standardBackstackNavigator.navigate(activity, request)
        }
    }

    private fun groupNavigate(
        activity: FragmentActivity,
        request: DestinationData
    ): Fragment {
        return groupNavigator.navigate(activity, request)
    }
}