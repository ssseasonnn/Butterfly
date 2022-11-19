package zlc.season.butterfly.compose

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper.contentView
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.compose.Utils.isComposeEntry
import zlc.season.butterfly.group.GroupEntry
import zlc.season.butterfly.group.GroupEntryManager
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter
import zlc.season.claritypotion.ClarityPotion.application

class AgileComposableLauncher(
    val backStackEntryManager: BackStackEntryManager,
    val groupEntryManager: GroupEntryManager
) {
    private val composeViewId = View.generateViewId()

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            @SuppressWarnings("deprecation")
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is FragmentActivity && savedInstanceState != null) {
                    val topEntry = backStackEntryManager.getTopEntry(activity)
                    if (topEntry != null && isComposeEntry(topEntry)) {
                        activity.contentView().post {
                            activity.directLaunch(topEntry.request)
                        }
                    }

                    val groupEntryList = groupEntryManager.getGroupList(activity, "")
                    val groupEntry = groupEntryList.firstOrNull { isComposeEntry(it) }
                    if (groupEntry != null) {
                        activity.contentView().post {
                            activity.directLaunch(groupEntry.request)
                        }
                    }
                }
            }
        })
    }

    fun FragmentActivity.clear() {
        val composeView = findViewById<ComposeView>(composeViewId)
        composeView?.setContent { }
    }

    fun FragmentActivity.retreat(request: AgileRequest) {
        directLaunch(request)
    }

    fun FragmentActivity.launch(request: AgileRequest) {
        if (request.groupId.isNotEmpty()) {
            groupLaunch(request)
        } else {
            if (request.clearTop) {
                clearTopLaunch(request)
            } else if (request.singleTop) {
                singleTopLaunch(request)
            } else {
                standardLaunch(request)
            }
        }
    }

    private fun FragmentActivity.groupLaunch(request: AgileRequest) {
        directLaunch(request)
        groupEntryManager.addEntity(this, GroupEntry(request))
    }

    private fun FragmentActivity.standardLaunch(request: AgileRequest) {
        if (request.enableBackStack) {
            backStackEntryManager.addEntry(this, BackStackEntry(request))
        }

        directLaunch(request)
    }

    private fun FragmentActivity.clearTopLaunch(request: AgileRequest) {
        val topEntryList = backStackEntryManager.getTopEntryList(this, request)
        return if (topEntryList.isEmpty()) {
            standardLaunch(request)
        } else {
            topEntryList.removeFirst()
            backStackEntryManager.removeEntries(this, topEntryList)
            directLaunch(request)
        }
    }

    private fun FragmentActivity.singleTopLaunch(request: AgileRequest) {
        val topEntry = backStackEntryManager.getTopEntry(this)
        return if (topEntry != null && topEntry.request.className == request.className) {
            directLaunch(request)
        } else {
            standardLaunch(request)
        }
    }

    private fun FragmentActivity.directLaunch(request: AgileRequest) {
        var composeView = findViewById<ComposeView>(composeViewId)
        if (composeView == null) {
            composeView = ComposeView(this).apply { id = composeViewId }
            val containerView = findContainerView(request)
            containerView.addView(composeView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
        invokeCompose(composeView, request)
    }

    private fun FragmentActivity.invokeCompose(composeView: ComposeView, request: AgileRequest) {
        composeView.tag = request.uniqueId

        val cls = Class.forName(request.className)
        val composable = cls.newInstance() as AgileComposable
        composeView.setContent {
            if (composable.paramsViewModelComposable != null) {
                composable.paramsViewModelComposable?.invoke(request.bundle, getViewModel(composable))
            } else if (composable.viewModelComposable != null) {
                composable.viewModelComposable?.invoke(getViewModel(composable))
            } else if (composable.paramsComposable != null) {
                composable.paramsComposable?.invoke(request.bundle)
            } else {
                composable.composable?.invoke()
            }
        }
    }

    private fun FragmentActivity.getViewModel(composable: AgileComposable): ViewModel {
        return ViewModelProvider(
            viewModelStore,
            defaultViewModelProviderFactory
        ).get(Class.forName(composable.viewModelClass) as Class<ViewModel>)
    }

    private fun FragmentActivity.findContainerView(request: AgileRequest): ViewGroup {
        var result: ViewGroup? = null
        if (request.containerViewId != 0) {
            result = findViewById(request.containerViewId)
        }
        if (result == null) {
            result = contentView()
        }
        return result
    }
}