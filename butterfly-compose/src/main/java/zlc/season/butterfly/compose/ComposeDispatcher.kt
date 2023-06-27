package zlc.season.butterfly.compose

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.compose.Utils.clearContainerView
import zlc.season.butterfly.compose.Utils.hasContainer
import zlc.season.butterfly.compose.Utils.hasSameContainer
import zlc.season.butterfly.compose.Utils.isActivityEntry
import zlc.season.butterfly.compose.Utils.isComposeEntry
import zlc.season.butterfly.compose.launcher.ComposeLauncher
import zlc.season.butterfly.dispatcher.InnerDispatcher
import zlc.season.butterfly.group.GroupEntryManager
import zlc.season.butterfly.internal.ButterflyHelper.setActivityResult

class ComposeDispatcher(
    private val backStackEntryManager: BackStackEntryManager,
    private val groupEntryManager: GroupEntryManager
) : InnerDispatcher {
    private val composeLauncher = ComposeLauncher(backStackEntryManager, groupEntryManager)

    override fun retreat(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {
        if (activity !is ComponentActivity) return

        // 由于栈顶的页面出栈，所以清除对应Entry的ViewModel
        ComposeViewModel.getInstance(activity.viewModelStore).clear(topEntry.request.uniqueTag)

        with(activity) {
            if (topEntry.hasContainer()) {
                // 如果当前的回退栈中没有container和栈顶页面相同的页面，则清空栈顶页面的container
                val entryList = backStackEntryManager.getEntryList(activity)
                val sameContainerEntry = entryList.find { it.hasSameContainer(topEntry) }
                if (sameContainerEntry == null) {
                    clearContainerView(topEntry.request)
                }
            }

            val newTopEntry = backStackEntryManager.getTopEntry(this)
            if (newTopEntry == null || isActivityEntry(newTopEntry)) {
                if (topEntry.request.isRoot) {
                    // 如果栈顶的页面是Root页面，并且回退之后新的栈顶页面为空或者为Activity，
                    // 则finish掉当前的activity
                    setActivityResult(bundle)
                    finish()
                } else {
                    // 反之则只清空当前的ComposeView Container
                    clearContainerView(topEntry.request)
                }
            }

            // launch new top entry directly
            if (newTopEntry != null && isComposeEntry(newTopEntry)) {
                with(composeLauncher) {
                    activity.launchDirectly(newTopEntry.request)
                }
            }
        }
    }

    override suspend fun dispatch(context: Context, request: AgileRequest): Flow<Result<Bundle>> {
        if (context is ComponentActivity) {
            with(composeLauncher) {
                context.launch(request)
            }
        }
        return emptyFlow()
    }
}