package zlc.season.butterfly.compose.launcher

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.compose.Utils.isComposeEntry
import zlc.season.butterfly.core.GroupEntryManager
import zlc.season.butterfly.entities.DestinationData
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
        application.registerActivityLifecycleCallbacks(object :
            ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is ComponentActivity && savedInstanceState != null) {
                    // restore top entry after activity recreated
                    val topEntry = backStackEntryManager.getTopEntry(activity)
                    if (topEntry != null && isComposeEntry(topEntry)) {
                        activity.contentView().post {
                            activity.launchDirectly(topEntry.destinationData)
                        }
                    }

                    // restore group entry after activity recreated
                    val groupEntryList = groupEntryManager.getGroupList(activity, "")
                    val groupEntry = groupEntryList.firstOrNull { isComposeEntry(it) }
                    if (groupEntry != null) {
                        activity.contentView().post {
                            activity.launchDirectly(groupEntry.destinationData)
                        }
                    }
                }
            }
        })
    }

    fun ComponentActivity.launchDirectly(data: DestinationData) {
        with(composeModeLauncher) { launchDirectly(data) }
    }

    fun ComponentActivity.launch(data: DestinationData) {
        if (data.groupId.isNotEmpty()) {
            with(composeGroupLauncher) { launch(data) }
        } else {
            with(composeModeLauncher) { launch(data) }
        }
    }
}