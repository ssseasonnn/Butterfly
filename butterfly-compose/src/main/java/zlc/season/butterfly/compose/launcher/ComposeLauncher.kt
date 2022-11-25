package zlc.season.butterfly.compose.launcher

import android.app.Activity
import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.compose.Utils.composeViewId
import zlc.season.butterfly.compose.Utils.isComposeEntry
import zlc.season.butterfly.group.GroupEntryManager
import zlc.season.butterfly.internal.ButterflyHelper.contentView
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter
import zlc.season.claritypotion.ClarityPotion.application

class ComposeLauncher(
    val backStackEntryManager: BackStackEntryManager,
    val groupEntryManager: GroupEntryManager
) {
    private val groupLauncher = GroupLauncher(groupEntryManager)
    private val modeLauncher = ModeLauncher(backStackEntryManager)

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is FragmentActivity && savedInstanceState != null) {
                    val topEntry = backStackEntryManager.getTopEntry(activity)
                    if (topEntry != null && isComposeEntry(topEntry)) {
                        activity.contentView().post {
                            activity.launchDirectly(topEntry.request)
                        }
                    }

                    val groupEntryList = groupEntryManager.getGroupList(activity, "")
                    val groupEntry = groupEntryList.firstOrNull { isComposeEntry(it) }
                    if (groupEntry != null) {
                        activity.contentView().post {
                            activity.launchDirectly(groupEntry.request)
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
        launchDirectly(request)
    }

    private fun FragmentActivity.launchDirectly(request: AgileRequest) {
        with(modeLauncher) { launchDirectly(request) }
    }

    fun FragmentActivity.launch(request: AgileRequest) {
        if (request.groupId.isNotEmpty()) {
            with(groupLauncher) { launch(request) }
        } else {
            with(modeLauncher) { launch(request) }
        }
    }
}