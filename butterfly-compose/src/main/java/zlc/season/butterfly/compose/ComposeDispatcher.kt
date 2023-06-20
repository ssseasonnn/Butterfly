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

        with(activity) {
            val newTopEntry = backStackEntryManager.getTopEntry(this)
            if (newTopEntry == null || isActivityEntry(newTopEntry)) {
                if (topEntry.request.isRoot) {
                    setActivityResult(bundle)
                    finish()
                } else {
                    with(composeLauncher) {
                        clear()
                    }
                }
            }
        }
    }

    override fun onRetreat(activity: Activity, topEntry: BackStackEntry) {
        if (activity !is ComponentActivity) return
        val newTopEntry = backStackEntryManager.getTopEntry(activity)
        if (newTopEntry != null && isComposeEntry(newTopEntry)) {
            with(composeLauncher) {
                activity.retreat(newTopEntry.request)
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