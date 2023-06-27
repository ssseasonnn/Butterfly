package zlc.season.butterfly.compose.launcher

import androidx.activity.ComponentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.compose.ComposeViewModel
import zlc.season.butterfly.compose.Utils.clearContainerView
import zlc.season.butterfly.compose.Utils.hasContainer
import zlc.season.butterfly.compose.Utils.hasSameContainer

class ComposeModeLauncher(private val backStackEntryManager: BackStackEntryManager) {
    private val composeBasicLauncher = ComposeBasicLauncher()

    fun ComponentActivity.launch(request: AgileRequest) {
        if (request.clearTop) {
            clearTopLaunch(request)
        } else if (request.singleTop) {
            singleTopLaunch(request)
        } else {
            standardLaunch(request)
        }
    }

    fun ComponentActivity.launchDirectly(request: AgileRequest) {
        basicLaunch(request)
    }

    private fun ComponentActivity.standardLaunch(request: AgileRequest) {
        if (request.enableBackStack) {
            backStackEntryManager.addEntry(this, BackStackEntry(request))
        }

        basicLaunch(request)
    }

    private fun ComponentActivity.clearTopLaunch(request: AgileRequest) {
        val topEntryList = backStackEntryManager.getTopEntryList(this, request)
        return if (topEntryList.isEmpty()) {
            standardLaunch(request)
        } else {
            val oldTopEntry = topEntryList.removeFirst()
            backStackEntryManager.removeEntries(this, topEntryList)

            val currentEntryList = backStackEntryManager.getEntryList(this)

            topEntryList.forEach { each ->
                // 清除出栈页面对应Entry的ViewModel
                ComposeViewModel.getInstance(viewModelStore).clear(each.request.uniqueTag)

                if (each.hasContainer()) {
                    // 如果当前的回退栈中没有和栈顶页面具有相同container的页面，则清空栈顶页面的container
                    val sameContainerEntry = currentEntryList.find { it.hasSameContainer(each) }
                    if (sameContainerEntry == null) {
                        clearContainerView(each.request)
                    }
                }
            }

            val sameTagRequest = request.copy(uniqueTag = oldTopEntry.request.uniqueTag)
            basicLaunch(sameTagRequest)
        }
    }

    private fun ComponentActivity.singleTopLaunch(request: AgileRequest) {
        val topEntry = backStackEntryManager.getTopEntry(this)
        return if (topEntry != null && topEntry.request.className == request.className) {
            val sameTagRequest = request.copy(uniqueTag = topEntry.request.uniqueTag)
            basicLaunch(sameTagRequest)
        } else {
            standardLaunch(request)
        }
    }

    private fun ComponentActivity.basicLaunch(request: AgileRequest) {
        with(composeBasicLauncher) {
            launch(request)
        }
    }
}