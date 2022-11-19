package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult
import zlc.season.butterfly.ButterflyHelper.createDialogFragment
import zlc.season.butterfly.ButterflyHelper.findDialogFragment
import zlc.season.butterfly.ButterflyHelper.setFragmentResult
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager

class DialogFragmentDispatcher(val backStackEntryManager: BackStackEntryManager) : InnerDispatcher {

    override fun retreat(activity: FragmentActivity, topEntry: BackStackEntry, bundle: Bundle) {
        with(activity) {
            val find = findDialogFragment(topEntry.request) ?: return
            setFragmentResult(topEntry.request.uniqueId, bundle)
            find.dismissAllowingStateLoss()
        }
    }

    override suspend fun dispatch(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> {
        if (request.enableBackStack) {
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


