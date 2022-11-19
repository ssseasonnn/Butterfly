package zlc.season.butterfly.dispatcher

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult
import zlc.season.butterfly.ButterflyHelper.findFragment
import zlc.season.butterfly.ButterflyHelper.remove
import zlc.season.butterfly.ButterflyHelper.setActivityResult
import zlc.season.butterfly.ButterflyHelper.setFragmentResult
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.dispatcher.launcher.FragmentLauncherContext
import zlc.season.butterfly.group.GroupEntryManager

class FragmentDispatcher(
    val backStackEntryManager: BackStackEntryManager,
    val groupEntryManager: GroupEntryManager
) : InnerDispatcher {

    private val fragmentLauncherContext = FragmentLauncherContext()

    override fun retreat(activity: FragmentActivity, topEntry: BackStackEntry, bundle: Bundle) {
        activity.apply {
            val newTopEntry = backStackEntryManager.getTopEntry(this)
            if ((newTopEntry == null || isActivityEntry(newTopEntry)) && topEntry.request.isRoot) {
                setActivityResult(bundle)
                finish()
            } else {
                findFragment(topEntry.request)?.let {
                    setFragmentResult(topEntry.request.uniqueId, bundle)
                    remove(it)
                }
            }
        }
    }

    override suspend fun dispatch(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> {
        val fragment = with(fragmentLauncherContext) {
            activity.launch(backStackEntryManager, groupEntryManager, request)
        }

        return if (request.needResult) {
            activity.awaitFragmentResult(fragment, request.uniqueId)
        } else {
            flowOf(Result.success(Bundle()))
        }
    }

    private fun isActivityEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.request.className)
        return Activity::class.java.isAssignableFrom(cls)
    }
}