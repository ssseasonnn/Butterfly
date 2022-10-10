package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult
import zlc.season.butterfly.ButterflyHelper.createFragment
import zlc.season.butterfly.ButterflyHelper.setFragmentResult
import zlc.season.butterfly.backstack.DialogFragmentEntry
import zlc.season.butterfly.backstack.DialogFragmentEntryManager
import zlc.season.butterfly.parseScheme
import java.lang.ref.WeakReference

object DialogFragmentDispatcher : InnerDispatcher {
    private val dialogFragmentEntryManager = DialogFragmentEntryManager()

    override fun retreatCount(): Int {
        val activity = ButterflyHelper.fragmentActivity ?: return 0
        return dialogFragmentEntryManager.getEntrySize(activity)
    }

    override fun retreat(bundle: Bundle): Boolean {
        val activity = ButterflyHelper.fragmentActivity ?: return false
        val topEntry = dialogFragmentEntryManager.getTopEntry(activity)
        topEntry?.let { entry ->
            dialogFragmentEntryManager.removeEntry(activity, entry)
            entry.reference.get()?.let {
                activity.setFragmentResult(entry.request.hashCode().toString(), bundle)
                it.dismissAllowingStateLoss()
                return true
            }
        }
        return false
    }

    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val activity = ButterflyHelper.fragmentActivity ?: return flowOf(Result.failure(IllegalStateException("Activity not found")))

        val fragment = activity.createFragment(request).apply {
            arguments = request.bundle
        }

        if (fragment is DialogFragment) {
            if (request.fragmentConfig.enableBackStack) {
                dialogFragmentEntryManager.addEntry(activity, DialogFragmentEntry(request, WeakReference(fragment)))
            }

            val realTag = request.fragmentConfig.tag.ifEmpty { parseScheme(request.scheme) }
            fragment.show(activity.supportFragmentManager, realTag)

            return if (request.needResult) {
                activity.awaitFragmentResult(fragment, request.hashCode().toString())
            } else {
                flowOf(Result.success(Bundle()))
            }
        }

        return flowOf(Result.failure(IllegalStateException("Needed DialogFragment.")))
    }
}