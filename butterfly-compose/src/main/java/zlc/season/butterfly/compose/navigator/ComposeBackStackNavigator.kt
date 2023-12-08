package zlc.season.butterfly.compose.navigator

import androidx.activity.ComponentActivity
import zlc.season.butterfly.compose.ComposeViewModel
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData

class ComposeBackStackNavigator(private val backStackEntryManager: BackStackEntryManager) {

    fun navigate(activity: ComponentActivity, data: DestinationData) {
        if (data.clearTop) {
            clearTopNavigate(activity, data)
        } else if (data.singleTop) {
            singleTopNavigate(activity, data)
        } else {
            standardNavigate(activity, data)
        }
    }

    private fun standardNavigate(activity: ComponentActivity, data: DestinationData) {
        if (data.enableBackStack) {
            backStackEntryManager.addEntry(activity, BackStackEntry(data))
        }

        ComposeNavigatorHelper.navigate(activity, data)
    }

    private fun clearTopNavigate(activity: ComponentActivity, data: DestinationData) {
        val topEntryList = backStackEntryManager.getTopEntryList(activity, data)
        return if (topEntryList.isEmpty()) {
            standardNavigate(activity, data)
        } else {
            // do not remove target entry.
            val topEntry = topEntryList.removeFirst()
            backStackEntryManager.removeEntryList(activity, topEntryList)

            // Clear removed entry resources.
            topEntryList.forEach { each ->
                // Clear ViewModel
                ComposeViewModel.getInstance(activity.viewModelStore)
                    .clear(each.destinationData.uniqueTag)

                // Clear view content
                ComposeNavigatorHelper.clearComposeView(activity, each.destinationData)
            }

            // update old destination's bundle data
            val newData = topEntry.destinationData.copy(bundle = data.bundle)
            ComposeNavigatorHelper.navigate(activity, newData)
        }
    }

    private fun singleTopNavigate(activity: ComponentActivity, data: DestinationData) {
        val topEntry = backStackEntryManager.getTopEntry(activity)
        return if (topEntry != null && topEntry.destinationData.className == data.className) {
            // update old destination's bundle data
            val newData = topEntry.destinationData.copy(bundle = data.bundle)
            ComposeNavigatorHelper.navigate(activity, newData)
        } else {
            standardNavigate(activity, data)
        }
    }
}