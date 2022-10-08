package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult
import zlc.season.butterfly.parseScheme

object FragmentDispatcher : InnerDispatcher {
    private val fragmentBackStackManager = FragmentBackStackManager()

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

    override fun retreat(bundle: Bundle) {
        val activity = ButterflyHelper.fragmentActivity ?: return
        fragmentBackStackManager.popFragment(activity, bundle)
    }

    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val activity = ButterflyHelper.fragmentActivity ?: return flowOf(Result.failure(IllegalStateException("No FragmentActivity exists.")))
        val fragmentManager = activity.supportFragmentManager
        val realRequest = request.createRealRequest()

        val config = realRequest.fragmentConfig
        val fragment = if (config.clearTop) {
            fragmentManager.clearTopAndShowFragment(activity, realRequest)
        } else if (config.singleTop) {
            fragmentManager.showSingleTopFragment(activity, realRequest)
        } else {
            fragmentManager.showNewFragment(activity, realRequest)
        }

        return if (realRequest.needResult) {
            fragmentManager.awaitFragmentResult(activity, fragment)
        } else {
            flowOf(Result.success(Bundle()))
        }
    }

    private fun FragmentManager.showNewFragment(
        activity: FragmentActivity, request: AgileRequest
    ): Fragment = with(beginTransaction()) {
        val fragment = createFragment(activity, request)

        request.fragmentConfig.apply {
            if (useReplace) {
                replace(containerViewId, fragment, tag)
            } else {
                add(containerViewId, fragment, tag)
            }
            setCustomAnimations(enterAnim, exitAnim, 0, 0)
            if (enableBackStack) {
                fragmentBackStackManager.pushFragment(activity, fragment, request)
            }
        }

        commitAllowingStateLoss()
        return fragment
    }


    private fun FragmentManager.clearTopAndShowFragment(
        activity: FragmentActivity, request: AgileRequest
    ): Fragment = with(beginTransaction()) {
        val backStackList = fragmentBackStackManager.getBackStackList(activity)
        val index = backStackList.indexOfLast { it.request.className == request.className }
        if (index == -1) {
            return@with showNewFragment(activity, request)
        } else {
            if (index != backStackList.lastIndex) {
                for (i in index + 1 until backStackList.size) {
                    remove(backStackList[i].fragment)
                    backStackList.removeAt(i)
                }
            }
            val config = request.fragmentConfig
            val target = backStackList[index].fragment
            target.arguments = request.bundle

            if (target.isAdded) {
                show(target)
            } else {
                add(config.containerViewId, target, config.tag)
            }
            setCustomAnimations(config.enterAnim, config.exitAnim, 0, 0)
            commitAllowingStateLoss()
            return@with target
        }
    }

    private fun FragmentManager.showSingleTopFragment(
        activity: FragmentActivity, request: AgileRequest
    ): Fragment = with(beginTransaction()) {
        val backStackList = fragmentBackStackManager.getBackStackList(activity)
        val index = backStackList.indexOfLast { it.request.className == request.className }
        if (index == -1 || index != backStackList.lastIndex) {
            return@with showNewFragment(activity, request)
        } else {
            val config = request.fragmentConfig
            val target = backStackList[index].fragment
            target.arguments = request.bundle
            if (target.isAdded) {
                show(target)
            } else {
                add(config.containerViewId, target, config.tag)
            }
            setCustomAnimations(config.enterAnim, config.exitAnim, 0, 0)
            commitAllowingStateLoss()
            return@with target
        }
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