package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult
import zlc.season.butterfly.ButterflyHelper.findFragment
import zlc.season.butterfly.ButterflyHelper.remove
import zlc.season.butterfly.ButterflyHelper.setFragmentResult
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.dispatcher.launcher.FragmentLauncherContext
import zlc.season.butterfly.group.FragmentGroupManager

object FragmentDispatcher : InnerDispatcher<Fragment> {
    private val backStackEntryManager = BackStackEntryManager()
    private val fragmentGroupManager = FragmentGroupManager()
    private val fragmentLauncherContext = FragmentLauncherContext()

    override fun retreatCount(): Int {
        val activity = ButterflyHelper.fragmentActivity ?: return 0
        return backStackEntryManager.getEntrySize(activity)
    }

    override fun retreat(target: Fragment?, bundle: Bundle): Boolean {
        val activity = ButterflyHelper.fragmentActivity ?: return false
        return if (target == null) {
            activity.retreatTop(bundle)
        } else {
            activity.retreatCurrent(target, bundle)
        }
    }

    private fun FragmentActivity.retreatTop(bundle: Bundle): Boolean {
        val topEntry = backStackEntryManager.getTopEntry(this) ?: return false
        backStackEntryManager.removeEntry(this, topEntry)

        val find = findFragment(topEntry.request) ?: return false
        setFragmentResult(topEntry.request.uniqueId, bundle)
        remove(find)
        return true
    }

    private fun FragmentActivity.retreatCurrent(target: Fragment, bundle: Bundle): Boolean {
        val findEntry = backStackEntryManager.findEntry(this) {
            it.request.uniqueId == target.tag
        }
        if (findEntry != null) {
            backStackEntryManager.removeEntry(this, findEntry)
            setFragmentResult(findEntry.request.uniqueId, bundle)
        }
        remove(target)
        return true
    }

    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val activity = ButterflyHelper.fragmentActivity ?: return flowOf(Result.failure(IllegalStateException("No FragmentActivity exists.")))

        val fragment = with(fragmentLauncherContext) {
            activity.launch(backStackEntryManager, fragmentGroupManager, request)
        }

        return if (request.needResult) {
            activity.awaitFragmentResult(fragment, request.uniqueId)
        } else {
            flowOf(Result.success(Bundle()))
        }
    }
}