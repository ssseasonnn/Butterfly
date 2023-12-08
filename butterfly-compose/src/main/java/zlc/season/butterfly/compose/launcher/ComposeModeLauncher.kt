package zlc.season.butterfly.compose.launcher

import androidx.activity.ComponentActivity
import zlc.season.butterfly.compose.ComposeViewModel
import zlc.season.butterfly.compose.Utils.clearContainerView
import zlc.season.butterfly.compose.Utils.hasContainer
import zlc.season.butterfly.compose.Utils.hasSameContainer
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData

class ComposeModeLauncher(private val backStackEntryManager: BackStackEntryManager) {
    private val composeBasicLauncher = ComposeBasicLauncher()

    fun ComponentActivity.launch(data: DestinationData) {
        if (data.clearTop) {
            clearTopLaunch(data)
        } else if (data.singleTop) {
            singleTopLaunch(data)
        } else {
            standardLaunch(data)
        }
    }

    fun ComponentActivity.launchDirectly(data: DestinationData) {
        basicLaunch(data)
    }

    private fun ComponentActivity.standardLaunch(data: DestinationData) {
        if (data.enableBackStack) {
            backStackEntryManager.addEntry(this, BackStackEntry(data))
        }

        basicLaunch(data)
    }

    private fun ComponentActivity.clearTopLaunch(data: DestinationData) {
        val topEntryList = backStackEntryManager.getTopEntryList(this, data)
        return if (topEntryList.isEmpty()) {
            standardLaunch(data)
        } else {
            val oldTopEntry = topEntryList.removeFirst()
            backStackEntryManager.removeEntryList(this, topEntryList)

            val currentEntryList = backStackEntryManager.getEntryList(this)

            topEntryList.forEach { each ->
                // 清除出栈页面对应Entry的ViewModel
                ComposeViewModel.getInstance(viewModelStore).clear(each.destinationData.uniqueTag)

                if (each.hasContainer()) {
                    // 如果当前的回退栈中没有和栈顶页面具有相同container的页面，则清空栈顶页面的container
                    val sameContainerEntry = currentEntryList.find { it.hasSameContainer(each) }
                    if (sameContainerEntry == null) {
                        clearContainerView(each.destinationData)
                    }
                }
            }

            val sameTagRequest = data.copy(uniqueTag = oldTopEntry.destinationData.uniqueTag)
            basicLaunch(sameTagRequest)
        }
    }

    private fun ComponentActivity.singleTopLaunch(data: DestinationData) {
        val topEntry = backStackEntryManager.getTopEntry(this)
        return if (topEntry != null && topEntry.destinationData.className == data.className) {
            val sameTagRequest = data.copy(uniqueTag = topEntry.destinationData.uniqueTag)
            basicLaunch(sameTagRequest)
        } else {
            standardLaunch(data)
        }
    }

    private fun ComponentActivity.basicLaunch(data: DestinationData) {
        with(composeBasicLauncher) {
            launch(data)
        }
    }
}