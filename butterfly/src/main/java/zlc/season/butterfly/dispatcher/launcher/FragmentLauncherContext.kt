package zlc.season.butterfly.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.group.GroupEntryManager

class FragmentLauncherContext : FragmentModeLauncher, FragmentGroupLauncher {
    private val standardLauncher = StandardLauncher()
    private val clearTopLauncher = ClearTopLauncher()
    private val singleTopLauncher = SingleTopLauncher()

    private val groupLauncher = GroupLauncher()

    fun FragmentActivity.launch(
        backStackEntryManager: BackStackEntryManager,
        groupEntryManager: GroupEntryManager,
        request: AgileRequest
    ): Fragment {
        return if (request.groupId.isNotEmpty()) {
            launch(groupEntryManager, request)
        } else {
            launch(backStackEntryManager, request)
        }
    }

    override fun FragmentActivity.launch(backStackEntryManager: BackStackEntryManager, request: AgileRequest): Fragment {
        return if (request.clearTop) {
            with(clearTopLauncher) { launch(backStackEntryManager, request) }
        } else if (request.singleTop) {
            with(singleTopLauncher) { launch(backStackEntryManager, request) }
        } else {
            with(standardLauncher) { launch(backStackEntryManager, request) }
        }
    }

    override fun FragmentActivity.launch(groupEntryManager: GroupEntryManager, request: AgileRequest): Fragment {
        return with(groupLauncher) { launch(groupEntryManager, request) }
    }
}