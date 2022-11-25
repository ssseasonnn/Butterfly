package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.internal.awaitFragmentResult
import zlc.season.butterfly.internal.findDialogFragment
import zlc.season.butterfly.internal.setFragmentResult
import zlc.season.butterfly.internal.showDialogFragment

class DialogFragmentDispatcher(val backStackEntryManager: BackStackEntryManager) : InnerDispatcher {

    override fun retreat(activity: FragmentActivity, topEntry: BackStackEntry, bundle: Bundle) {
        with(activity) {
            val find = findDialogFragment(topEntry.request) ?: return
            setFragmentResult(topEntry.request.uniqueId, bundle)
            find.dismissAllowingStateLoss()
        }
    }

    override suspend fun dispatchByActivity(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> {
        if (request.enableBackStack) {
            backStackEntryManager.addEntry(activity, BackStackEntry(request))
        }

        val dialogFragment = activity.showDialogFragment(request)
        return if (request.needResult) {
            activity.awaitFragmentResult(dialogFragment, request.uniqueId)
        } else {
            emptyFlow()
        }
    }
}


