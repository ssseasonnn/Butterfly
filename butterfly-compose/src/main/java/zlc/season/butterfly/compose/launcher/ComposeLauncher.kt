package zlc.season.butterfly.compose.launcher

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.compose.Utils.isComposeEntry
import zlc.season.butterfly.group.GroupEntryManager
import zlc.season.butterfly.internal.ButterflyHelper.contentView
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter
import zlc.season.claritypotion.ClarityPotion.application

class ComposeLauncher(
    val backStackEntryManager: BackStackEntryManager,
    val groupEntryManager: GroupEntryManager
) {
    private val composeGroupLauncher = ComposeGroupLauncher(groupEntryManager)
    private val composeModeLauncher = ComposeModeLauncher(backStackEntryManager)

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is ComponentActivity && savedInstanceState != null) {
                    // restore top entry after activity recreated
                    val topEntry = backStackEntryManager.getTopEntry(activity)
                    if (topEntry != null && isComposeEntry(topEntry)) {
                        activity.contentView().post {
                            activity.launchDirectly(topEntry.request)
                        }
                    }

                    // restore group entry after activity recreated
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

    fun ComponentActivity.launchDirectly(request: AgileRequest) {
        with(composeModeLauncher) { launchDirectly(request) }
    }

    fun ComponentActivity.launch(request: AgileRequest) {
        if (request.groupId.isNotEmpty()) {
            with(composeGroupLauncher) { launch(request) }
        } else {
            with(composeModeLauncher) { launch(request) }
        }
    }
}