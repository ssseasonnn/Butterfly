package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.Butterfly.setResult
import zlc.season.butterfly.ButterflyHelper
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult

object DialogFragmentDispatcher : InnerDispatcher {
    override fun retreat(request: AgileRequest, bundle: Bundle) {
        val fragmentManager = ButterflyHelper.fragmentManager ?: return
        if (request.fragmentConfig.tag.isNotEmpty()) {
            val dialogFragment = fragmentManager.findFragmentByTag(request.fragmentConfig.tag)
            if (dialogFragment != null && dialogFragment is DialogFragment) {
                dialogFragment.setResult(bundle)
                dialogFragment.dismissAllowingStateLoss()
            }
        }
    }

    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val activity = ButterflyHelper.fragmentActivity ?: return flowOf(Result.failure(IllegalStateException("Activity not found")))

        val fragment = createFragment(activity, request)
        val fragmentManager = activity.supportFragmentManager

        if (fragment is DialogFragment) {
            fragment.show(fragmentManager, fragment.javaClass.name)
            return if (request.needResult) {
                fragmentManager.awaitFragmentResult(activity, fragment)
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