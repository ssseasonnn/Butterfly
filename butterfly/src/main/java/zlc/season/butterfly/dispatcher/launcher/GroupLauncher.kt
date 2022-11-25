package zlc.season.butterfly.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.group.GroupEntry
import zlc.season.butterfly.group.GroupEntryManager
import zlc.season.butterfly.internal.findFragment
import zlc.season.butterfly.internal.hideFragment
import zlc.season.butterfly.internal.showFragment
import zlc.season.butterfly.internal.showNewFragment

class GroupLauncher : FragmentGroupLauncher {
    override fun FragmentActivity.launch(groupEntryManager: GroupEntryManager, request: AgileRequest): Fragment {
        val list = groupEntryManager.getGroupList(this, request.groupId)
        list.forEach { entity ->
            findFragment(entity.request)?.also { hideFragment(it) }
        }

        val targetEntry = list.find { it.request.className == request.className }
        val targetFragment = targetEntry?.run {
            findFragment(targetEntry.request)
        }

        return if (targetFragment == null) {
            if (targetEntry == null) {
                groupEntryManager.addEntry(this, GroupEntry(request))
            }
            showNewFragment(request)
        } else {
            if (targetFragment is OnFragmentNewArgument) {
                targetFragment.onNewArgument(request.bundle)
            }
            showFragment(targetFragment)
            targetFragment
        }
    }
}