package zlc.season.butterfly.compose.launcher

import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager

class ModeLauncher(private val backStackEntryManager: BackStackEntryManager) {
    private val commonLauncher = CommonLauncher()

    fun FragmentActivity.launch(request: AgileRequest) {
        if (request.clearTop) {
            clearTopLaunch(request)
        } else if (request.singleTop) {
            singleTopLaunch(request)
        } else {
            standardLaunch(request)
        }
    }

    fun FragmentActivity.launchDirectly(request: AgileRequest) {
        commonLaunch(request)
    }

    private fun FragmentActivity.standardLaunch(request: AgileRequest) {
        if (request.enableBackStack) {
            backStackEntryManager.addEntry(this, BackStackEntry(request))
        }

        commonLaunch(request)
    }

    private fun FragmentActivity.clearTopLaunch(request: AgileRequest) {
        val topEntryList = backStackEntryManager.getTopEntryList(this, request)
        return if (topEntryList.isEmpty()) {
            standardLaunch(request)
        } else {
            topEntryList.removeFirst()
            backStackEntryManager.removeEntries(this, topEntryList)
            commonLaunch(request)
        }
    }

    private fun FragmentActivity.singleTopLaunch(request: AgileRequest) {
        val topEntry = backStackEntryManager.getTopEntry(this)
        return if (topEntry != null && topEntry.request.className == request.className) {
            commonLaunch(request)
        } else {
            standardLaunch(request)
        }
    }

    private fun FragmentActivity.commonLaunch(request: AgileRequest) {
        with(commonLauncher) {
            launch(request)
        }
    }
}