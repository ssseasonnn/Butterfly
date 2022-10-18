package zlc.season.butterfly.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.group.FragmentGroupManager

class FragmentLauncherContext : FragmentModeLauncher, FragmentGroupLauncher {
    private val standardLauncher = StandardLauncher()
    private val clearTopLauncher = ClearTopLauncher()
    private val singleTopLauncher = SingleTopLauncher()

    private val groupLauncher = GroupLauncher()

    fun FragmentActivity.launch(
        backStackEntryManager: BackStackEntryManager,
        fragmentGroupManager: FragmentGroupManager,
        request: AgileRequest
    ): Fragment {
        return if (request.fragmentConfig.groupId.isNotEmpty()) {
            launch(fragmentGroupManager, request)
        } else {
            launch(backStackEntryManager, request)
        }
    }

    override fun FragmentActivity.launch(backStackEntryManager: BackStackEntryManager, request: AgileRequest): Fragment {
        return if (request.fragmentConfig.clearTop) {
            with(clearTopLauncher) { launch(backStackEntryManager, request) }
        } else if (request.fragmentConfig.singleTop) {
            with(singleTopLauncher) { launch(backStackEntryManager, request) }
        } else {
            with(standardLauncher) { launch(backStackEntryManager, request) }
        }
    }

    override fun FragmentActivity.launch(fragmentGroupManager: FragmentGroupManager, request: AgileRequest): Fragment {
        return with(groupLauncher) { launch(fragmentGroupManager, request) }
    }
}