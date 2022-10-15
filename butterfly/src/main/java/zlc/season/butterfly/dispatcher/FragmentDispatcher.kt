package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult
import zlc.season.butterfly.ButterflyHelper.createFragment
import zlc.season.butterfly.ButterflyHelper.hide
import zlc.season.butterfly.ButterflyHelper.remove
import zlc.season.butterfly.ButterflyHelper.setFragmentResult
import zlc.season.butterfly.ButterflyHelper.show
import zlc.season.butterfly.backstack.FragmentEntry
import zlc.season.butterfly.backstack.FragmentEntryManager
import zlc.season.butterfly.backstack.OnFragmentNewArgument
import zlc.season.butterfly.group.FragmentGroupEntity
import zlc.season.butterfly.group.FragmentGroupManager
import zlc.season.butterfly.parseScheme
import java.lang.ref.WeakReference

object FragmentDispatcher : InnerDispatcher {
    private val fragmentEntryManager = FragmentEntryManager()
    private val fragmentGroupManager = FragmentGroupManager()

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
            if (realRequest.fragmentConfig.groupName.isNotEmpty()) {
                handleGroup(realRequest)
            } else {
                if (realRequest.fragmentConfig.clearTop) {
                    handleClearTop(realRequest)
                } else if (realRequest.fragmentConfig.singleTop) {
                    handleSingleTop(realRequest)
                } else {
                    handleNormal(realRequest)
                }
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

    private fun FragmentActivity.handleGroup(request: AgileRequest): Fragment {
        val list = fragmentGroupManager.getGroupList(this, request)
        val target = list.find { it.request.className == request.className }
        val targetFragment = target?.reference?.get()

        list.forEach { it.reference.get()?.apply { hide(this) } }

        return if (targetFragment != null) {
            if (targetFragment is OnFragmentNewArgument) {
                targetFragment.onNewArgument(request.bundle)
            }
            show(targetFragment)

            targetFragment
        } else {
            val fragment = createFragment(request)
            fragmentGroupManager.addEntity(this, FragmentGroupEntity(request, WeakReference(fragment)))
            directShow(request, fragment)
        }
    }

    private fun FragmentActivity.handleNormal(request: AgileRequest): Fragment {
        val fragment = createFragment(request)
        if (request.fragmentConfig.enableBackStack) {
            fragmentEntryManager.addEntry(this, FragmentEntry(request, WeakReference(fragment)))
        }
        return commitTarget(request, fragment)
    }

    private fun FragmentActivity.handleClearTop(request: AgileRequest): Fragment? {
        val topEntryList = fragmentEntryManager.getTopEntryList(this, request)
        return if (topEntryList.isEmpty()) {
            handleNormal(request)
        } else {
            val targetEntry = topEntryList.removeFirst()
            topEntryList.forEach {
                it.reference.get()?.apply { remove(this) }
            }
            fragmentEntryManager.removeEntries(this, topEntryList)
            showAgain(request, targetEntry.reference)
        }
    }


    private fun FragmentActivity.handleSingleTop(request: AgileRequest): Fragment? {
        val topEntry = fragmentEntryManager.getTopEntry(this)
        return if (topEntry?.request?.className == request.className) {
            showAgain(request, topEntry.reference)
        } else {
            handleNormal(request)
        }
    }

    private fun FragmentActivity.showAgain(request: AgileRequest, weakReference: WeakReference<Fragment>): Fragment? {
        val target = weakReference.get() ?: return null

        if (target is OnFragmentNewArgument) {
            target.onNewArgument(request.bundle)
        }

        return commitTarget(request, target)
    }

    private fun FragmentActivity.commitTarget(request: AgileRequest, target: Fragment): Fragment {
        with(supportFragmentManager.beginTransaction()) {
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
        }

        return target
    }

    private fun FragmentActivity.directShow(request: AgileRequest, target: Fragment): Fragment {
        with(supportFragmentManager.beginTransaction()) {
            val config = request.fragmentConfig
            setCustomAnimations(config.enterAnim, config.exitAnim, 0, 0)
            add(config.containerViewId, target, config.tag)
            commitAllowingStateLoss()
        }

        return target
    }
}