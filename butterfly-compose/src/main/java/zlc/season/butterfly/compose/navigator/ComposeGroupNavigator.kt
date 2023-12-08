package zlc.season.butterfly.compose.navigator

import androidx.activity.ComponentActivity
import zlc.season.butterfly.core.GroupEntryManager
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.entities.GroupEntry

class ComposeGroupNavigator(private val groupEntryManager: GroupEntryManager) {

    fun navigate(activity: ComponentActivity, data: DestinationData) {
        val groupEntryList = groupEntryManager.getGroupList(activity, data.groupId)
        val find = groupEntryList.find { it.destinationData.className == data.className }
        if (find == null) {
            groupEntryManager.addEntry(activity, GroupEntry(data))
        }

        ComposeNavigatorHelper.navigate(activity, data)
    }
}