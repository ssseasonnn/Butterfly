package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult
import zlc.season.butterfly.ButterflyHelper.createFragment
import zlc.season.butterfly.ButterflyHelper.remove
import zlc.season.butterfly.ButterflyHelper.setFragmentResult
import zlc.season.butterfly.backstack.FragmentEntry
import zlc.season.butterfly.backstack.FragmentEntryManager
import zlc.season.butterfly.parseScheme
import java.lang.ref.WeakReference

object FragmentDispatcher : InnerDispatcher {
    private val fragmentEntryManager = FragmentEntryManager()

    private fun AgileRequest.createRealRequest(): AgileRequest {
        return copy(
            fragmentConfig = fragmentConfig.copy(
                containerViewId = if (fragmentConfig.containerViewId == 0) {
                    android.R.id.content
                } else {
                    fragmentConfig.containerViewId
                },
                tag = fragmentConfig.tag.ifEmpty { parseScheme(scheme) },
            )
        )
    }

    override fun retreatCount(): Int {
        val activity = ButterflyHelper.fragmentActivity ?: return 0
        return fragmentEntryManager.getEntrySize(activity)
    }

    override fun retreatDirectly(target: Any, bundle: Bundle): Boolean {
        if (target is Fragment) {
            val activity = ButterflyHelper.fragmentActivity ?: return false
            val findEntry = fragmentEntryManager.findEntry(activity) {
                it.reference.get() == target
            }
            findEntry?.let { entry ->
                fragmentEntryManager.removeEntry(activity, entry)
                entry.reference.get()?.let {
                    activity.setFragmentResult(entry.request.hashCode().toString(), bundle)
                    activity.remove(it)
                    return true
                }
            }
        }
        return false
    }

    override fun retreat(bundle: Bundle): Boolean {
        val activity = ButterflyHelper.fragmentActivity ?: return false
        val topEntry = fragmentEntryManager.getTopEntry(activity)
        topEntry?.let { entry ->
            fragmentEntryManager.removeEntry(activity, entry)
            entry.reference.get()?.let {
                activity.setFragmentResult(entry.request.hashCode().toString(), bundle)
                activity.remove(it)
                return true
            }
        }
        return false
    }

    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val activity = ButterflyHelper.fragmentActivity ?: return flowOf(Result.failure(IllegalStateException("No FragmentActivity exists.")))

        val realRequest = request.createRealRequest()
        val fragment = with(activity) {
            if (realRequest.fragmentConfig.clearTop) {
                clearTop(realRequest)
            } else if (realRequest.fragmentConfig.singleTop) {
                singleTop(realRequest)
            } else {
                normal(realRequest)
            }
        }

        return if (fragment == null) {
            flowOf(Result.failure(IllegalStateException("Fragment is NULL.")))
        } else if (realRequest.needResult) {
            activity.awaitFragmentResult(fragment, realRequest.hashCode().toString())
        } else {
            flowOf(Result.success(Bundle()))
        }
    }

    private fun FragmentActivity.normal(request: AgileRequest): Fragment =
        with(supportFragmentManager.beginTransaction()) {
            val fragment = createFragment(request)
            if (request.fragmentConfig.enableBackStack) {
                fragmentEntryManager.addEntry(this@normal, FragmentEntry(request, WeakReference(fragment)))
            }
            show(request, fragment)
        }

    private fun FragmentActivity.clearTop(request: AgileRequest): Fragment? =
        with(supportFragmentManager.beginTransaction()) {
            val topEntryList = fragmentEntryManager.getTopEntryList(this@clearTop, request)
            return if (topEntryList.isEmpty()) {
                normal(request)
            } else {
                val targetEntry = topEntryList.removeFirst()
                topEntryList.forEach {
                    it.reference.get()?.apply { remove(this) }
                }
                fragmentEntryManager.removeEntries(this@clearTop, topEntryList)

                show(request, targetEntry.reference)
            }
        }

    private fun FragmentActivity.singleTop(request: AgileRequest): Fragment? =
        with(supportFragmentManager.beginTransaction()) {
            val topEntry = fragmentEntryManager.getTopEntry(this@singleTop)
            return if (topEntry?.request?.className == request.className) {
                show(request, topEntry.reference)
            } else {
                normal(request)
            }
        }

    private fun FragmentTransaction.show(request: AgileRequest, weakReference: WeakReference<Fragment>): Fragment? {
        val target = weakReference.get() ?: return null
        return show(request, target)
    }

    private fun FragmentTransaction.show(request: AgileRequest, target: Fragment): Fragment {
        target.arguments = request.bundle

        val config = request.fragmentConfig
        setCustomAnimations(config.enterAnim, config.exitAnim, 0, 0)

        if (target.isAdded) {
            show(target)
        } else {
            if (config.useReplace) {
                replace(config.containerViewId, target, config.tag)
            } else {
                add(config.containerViewId, target, config.tag)
            }
        }

        commitAllowingStateLoss()

        return target
    }
}