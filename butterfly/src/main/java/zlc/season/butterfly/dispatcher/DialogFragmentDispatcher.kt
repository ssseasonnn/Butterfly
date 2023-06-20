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
import zlc.season.butterfly.internal.ButterflyFragment.Companion.awaitFragmentResult
import zlc.season.butterfly.internal.findDialogFragment
import zlc.season.butterfly.internal.setFragmentResult
import zlc.season.butterfly.internal.showDialogFragment

class DialogFragmentDispatcher(val backStackEntryManager: BackStackEntryManager) : InnerDispatcher {

    override suspend fun dispatch(context: Context, request: AgileRequest): Flow<Result<Bundle>> {
        return if (context is FragmentActivity) {
            dispatch(context, request)
        } else {
            emptyFlow()
        }
    }

    private fun dispatch(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> {
        if (request.enableBackStack) {
            backStackEntryManager.addEntry(activity, BackStackEntry(request))
        }

        activity.showDialogFragment(request)

        return if (request.needResult) {
            activity.awaitFragmentResult(request.scheme, request.uniqueTag)
        } else {
            emptyFlow()
        }
    }

    override fun retreat(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {
        if (activity !is FragmentActivity) return
        with(activity) {
            val find = findDialogFragment(topEntry.request) ?: return
            if (topEntry.request.needResult) {
                setFragmentResult(topEntry.request.uniqueTag, bundle)
            }
            find.dismissAllowingStateLoss()
        }
    }
}


