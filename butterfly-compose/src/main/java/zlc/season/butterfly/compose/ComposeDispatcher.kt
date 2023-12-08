package zlc.season.butterfly.compose

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import zlc.season.butterfly.compose.Utils.clearContainerView
import zlc.season.butterfly.compose.Utils.hasContainer
import zlc.season.butterfly.compose.Utils.hasSameContainer
import zlc.season.butterfly.compose.Utils.isActivityEntry
import zlc.season.butterfly.compose.Utils.isComposeEntry
import zlc.season.butterfly.compose.launcher.ComposeLauncher
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.core.GroupEntryManager
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.internal.ButterflyHelper.setActivityResult
import zlc.season.butterfly.navigator.Navigator

class ComposeNavigator(
    private val backStackEntryManager: BackStackEntryManager,
    private val groupEntryManager: GroupEntryManager
) : Navigator {
    private val composeLauncher = ComposeLauncher(backStackEntryManager, groupEntryManager)

    override fun popBack(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {
        if (activity !is ComponentActivity) return

        // 由于栈顶的页面出栈，所以清除对应Entry的ViewModel
        ComposeViewModel.getInstance(activity.viewModelStore)
            .clear(topEntry.destinationData.uniqueTag)

        with(activity) {
            if (topEntry.hasContainer()) {
                // 如果当前的回退栈中没有container和栈顶页面相同的页面，则清空栈顶页面的container
                val entryList = backStackEntryManager.getEntryList(activity)
                val sameContainerEntry = entryList.find { it.hasSameContainer(topEntry) }
                if (sameContainerEntry == null) {
                    clearContainerView(topEntry.destinationData)
                }
            }

            val newTopEntry = backStackEntryManager.getTopEntry(this)
            if (newTopEntry == null || isActivityEntry(newTopEntry)) {
                if (topEntry.destinationData.isRoot) {
                    // 如果栈顶的页面是Root页面，并且回退之后新的栈顶页面为空或者为Activity，
                    // 则finish掉当前的activity
                    setActivityResult(bundle)
                    finish()
                } else {
                    // 反之则只清空当前的ComposeView Container
                    clearContainerView(topEntry.destinationData)
                }
            }

            // launch new top entry directly
            if (newTopEntry != null && isComposeEntry(newTopEntry)) {
                with(composeLauncher) {
                    activity.launchDirectly(newTopEntry.destinationData)
                }
            }
        }
    }

    override suspend fun navigate(context: Context, data: DestinationData): Result<Bundle> {
        if (context is ComponentActivity) {
            with(composeLauncher) {
                context.launch(data)
            }
        }
        return Result.success(Bundle.EMPTY)
    }
}