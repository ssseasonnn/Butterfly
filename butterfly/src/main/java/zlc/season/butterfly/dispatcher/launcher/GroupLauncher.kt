package zlc.season.butterfly.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper.createFragment
import zlc.season.butterfly.ButterflyHelper.findFragment
import zlc.season.butterfly.ButterflyHelper.hide
import zlc.season.butterfly.ButterflyHelper.show
import zlc.season.butterfly.group.GroupEntry
import zlc.season.butterfly.group.GroupEntryManager

class GroupLauncher : FragmentGroupLauncher {
    override fun FragmentActivity.launch(groupEntryManager: GroupEntryManager, request: AgileRequest): Fragment {
        val list = groupEntryManager.getGroupList(this, request.groupId)
        list.forEach { entity ->
            findFragment(entity.request)?.also { hide(it) }
        }

        val target = list.find { it.request.className == request.className }?.run { findFragment(request) }

        return if (target == null) {
            groupEntryManager.addEntity(this, GroupEntry(request))
            show(request)
        } else {
            if (target is OnFragmentNewArgument) {
                target.onNewArgument(request.bundle)
            }
            show(target)
            target
        }
    }

    private fun FragmentActivity.show(request: AgileRequest): Fragment {
        val fragment = createFragment(request)
        with(supportFragmentManager.beginTransaction()) {
            setCustomAnimations(request.enterAnim, request.exitAnim, 0, 0)
            if (request.useReplace) {
                replace(request.containerId(), fragment, request.uniqueId)
            } else {
                add(request.containerId(), fragment, request.uniqueId)
            }

            commitAllowingStateLoss()
        }
        return fragment
    }

    private fun AgileRequest.containerId(): Int {
        return if (containerViewId != 0) containerViewId else android.R.id.content
    }
}