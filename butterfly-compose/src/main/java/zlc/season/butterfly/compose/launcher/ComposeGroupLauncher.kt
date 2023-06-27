package zlc.season.butterfly.compose.launcher

import androidx.activity.ComponentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.group.GroupEntry
import zlc.season.butterfly.group.GroupEntryManager

class ComposeGroupLauncher(private val groupEntryManager: GroupEntryManager) {
    private val composeBasicLauncher = ComposeBasicLauncher()

    fun ComponentActivity.launch(request: AgileRequest) {
        val groupEntryList = groupEntryManager.getGroupList(this, request.groupId)
        val find = groupEntryList.find { it.request.className == request.className }
        if (find == null) {
            groupEntryManager.addEntry(this, GroupEntry(request))
        }

        with(composeBasicLauncher) {
            launch(request)
        }
    }
}