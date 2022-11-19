package zlc.season.butterfly.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.group.GroupEntryManager

interface FragmentGroupLauncher {
    fun FragmentActivity.launch(groupEntryManager: GroupEntryManager, request: AgileRequest): Fragment
}