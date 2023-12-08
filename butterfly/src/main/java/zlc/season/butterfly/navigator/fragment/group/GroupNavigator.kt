package zlc.season.butterfly.navigator.fragment.group

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.core.GroupEntryManager
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.entities.GroupEntry
import zlc.season.butterfly.navigator.fragment.OnFragmentNewArgument
import zlc.season.butterfly.navigator.fragment.createAndShowFragment
import zlc.season.butterfly.navigator.fragment.findFragment
import zlc.season.butterfly.navigator.fragment.hideFragment
import zlc.season.butterfly.navigator.fragment.showFragment

class GroupNavigator(private val groupEntryManager: GroupEntryManager) {

    fun navigate(
        activity: FragmentActivity,
        request: DestinationData
    ): Fragment {
        val list = groupEntryManager.getGroupList(activity, request.groupId)
        list.forEach { entity ->
            activity.findFragment(entity.destinationData)?.also {
                activity.hideFragment(it)
            }
        }

        val targetEntry = list.find { it.destinationData.className == request.className }
        val targetFragment = targetEntry?.run {
            activity.findFragment(targetEntry.destinationData)
        }

        return if (targetFragment == null) {
            if (targetEntry == null) {
                groupEntryManager.addEntry(activity, GroupEntry(request))
            }
            activity.createAndShowFragment(request)
        } else {
            // pass new arguments to fragment
            if (targetFragment is OnFragmentNewArgument) {
                targetFragment.onNewArgument(request.bundle)
            }

            activity.showFragment(targetFragment)
            targetFragment
        }
    }
}