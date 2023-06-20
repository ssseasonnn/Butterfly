package zlc.season.butterfly.dispatcher

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.dispatcher.launcher.FragmentLauncherContext
import zlc.season.butterfly.group.GroupEntryManager
import zlc.season.butterfly.internal.ButterflyFragment.Companion.awaitFragmentResult
import zlc.season.butterfly.internal.ButterflyHelper.setActivityResult
import zlc.season.butterfly.internal.findFragment
import zlc.season.butterfly.internal.removeFragment
import zlc.season.butterfly.internal.setFragmentResult

class FragmentDispatcher(
    private val backStackEntryManager: BackStackEntryManager,
    private val groupEntryManager: GroupEntryManager
) : InnerDispatcher {

    private val fragmentLauncherContext = FragmentLauncherContext()

    override suspend fun dispatch(context: Context, request: AgileRequest): Flow<Result<Bundle>> {
        return if (context is FragmentActivity) {
            dispatch(context, request)
        } else {
            emptyFlow()
        }
    }

    private fun dispatch(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> {
        with(fragmentLauncherContext) {
            activity.launch(backStackEntryManager, groupEntryManager, request)
        }

        return if (request.needResult) {
            activity.awaitFragmentResult(request.scheme, request.uniqueTag)
        } else {
            emptyFlow()
        }
    }

    override fun retreat(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {
        if (activity !is FragmentActivity) return
        activity.apply {
            val newTopEntry = backStackEntryManager.getTopEntry(this)
            if ((newTopEntry == null || isActivityEntry(newTopEntry)) && topEntry.request.isRoot) {
                setActivityResult(bundle)
                finish()
            } else {
                findFragment(topEntry.request)?.let {
                    if (topEntry.request.needResult) {
                        setFragmentResult(topEntry.request.uniqueTag, bundle)
                    }
                    removeFragment(it)
                }
            }
        }
    }

    private fun isActivityEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.request.className)
        return Activity::class.java.isAssignableFrom(cls)
    }
}