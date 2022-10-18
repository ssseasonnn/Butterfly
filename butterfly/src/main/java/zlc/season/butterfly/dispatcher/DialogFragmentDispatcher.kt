package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult
import zlc.season.butterfly.ButterflyHelper.createDialogFragment
import zlc.season.butterfly.ButterflyHelper.findDialogFragment
import zlc.season.butterfly.ButterflyHelper.setFragmentResult
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager

object DialogFragmentDispatcher : InnerDispatcher<DialogFragment> {
    private val backStackEntryManager = BackStackEntryManager()

    override fun retreatCount(): Int {
        val activity = ButterflyHelper.fragmentActivity ?: return 0
        return backStackEntryManager.getEntrySize(activity)
    }

    override fun retreat(target: DialogFragment?, bundle: Bundle): Boolean {
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

        val find = findDialogFragment(topEntry.request) ?: return false
        setFragmentResult(topEntry.request.uniqueId, bundle)
        find.dismissAllowingStateLoss()
        return true
    }

    private fun FragmentActivity.retreatCurrent(target: DialogFragment, bundle: Bundle): Boolean {
        val findEntry = backStackEntryManager.findEntry(this) {
            it.request.uniqueId == target.tag
        }
        if (findEntry != null) {
            backStackEntryManager.removeEntry(this, findEntry)
            setFragmentResult(findEntry.request.uniqueId, bundle)
        }
        target.dismissAllowingStateLoss()
        return true
    }

    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val activity = ButterflyHelper.fragmentActivity ?: return flowOf(Result.failure(IllegalStateException("Activity not found")))

        if (request.fragmentConfig.enableBackStack) {
            backStackEntryManager.addEntry(activity, BackStackEntry(request))
        }

        val dialogFragment = activity.createDialogFragment(request)
        dialogFragment.show(activity.supportFragmentManager, request.uniqueId)
        return if (request.needResult) {
            activity.awaitFragmentResult(dialogFragment, request.uniqueId)
        } else {
            flowOf(Result.success(Bundle()))
        }
    }
}


