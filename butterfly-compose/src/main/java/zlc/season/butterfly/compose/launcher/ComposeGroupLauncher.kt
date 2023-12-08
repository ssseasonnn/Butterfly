package zlc.season.butterfly.compose.launcher

import androidx.activity.ComponentActivity
import zlc.season.butterfly.core.GroupEntryManager
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.entities.GroupEntry

class ComposeGroupLauncher(private val groupEntryManager: GroupEntryManager) {
    private val composeBasicLauncher = ComposeBasicLauncher()

    fun ComponentActivity.launch(data: DestinationData) {
        val groupEntryList = groupEntryManager.getGroupList(this, data.groupId)
        val find = groupEntryList.find { it.destinationData.className == data.className }
        if (find == null) {
            groupEntryManager.addEntry(this, GroupEntry(data))
        }

        with(composeBasicLauncher) {
            launch(data)
        }
    }
}