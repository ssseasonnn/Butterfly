package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult

object DialogFragmentDispatcher : InnerDispatcher {
    override fun retreat(bundle: Bundle): Boolean {
        return false
    }

    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val activity = ButterflyHelper.fragmentActivity ?: return flowOf(Result.failure(IllegalStateException("Activity not found")))

        val fragment = createFragment(activity, request)

        if (fragment is DialogFragment) {
            fragment.show(
                activity.supportFragmentManager, fragment.javaClass.name
            )
            return if (request.needResult) {
                activity.awaitFragmentResult(fragment)
            } else {
                flowOf(Result.success(Bundle()))
            }
        }

        return flowOf(Result.success(Bundle()))
    }

    private fun createFragment(
        activity: FragmentActivity, request: AgileRequest
    ): Fragment {
        val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            activity.classLoader, request.className
        )
        fragment.arguments = request.bundle
        return fragment
    }
}