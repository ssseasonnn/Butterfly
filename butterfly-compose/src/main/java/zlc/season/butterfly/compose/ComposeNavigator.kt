package zlc.season.butterfly.compose

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import zlc.season.butterfly.compose.Utils.isComposeEntry
import zlc.season.butterfly.compose.navigator.ComposeBackStackNavigator
import zlc.season.butterfly.compose.navigator.ComposeGroupNavigator
import zlc.season.butterfly.compose.navigator.ComposeNavigatorHelper
import zlc.season.butterfly.core.BackStackEntryManager
import zlc.season.butterfly.core.GroupEntryManager
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.internal.ButterflyHelper.application
import zlc.season.butterfly.internal.ButterflyHelper.contentView
import zlc.season.butterfly.internal.logd
import zlc.season.butterfly.navigator.Navigator
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter

class ComposeNavigator(
    private val backStackEntryManager: BackStackEntryManager,
    private val groupEntryManager: GroupEntryManager
) : Navigator {

    private val composeGroupNavigator = ComposeGroupNavigator(groupEntryManager)
    private val composeBackStackNavigator = ComposeBackStackNavigator(backStackEntryManager)

    init {
        application.registerActivityLifecycleCallbacks(object :
            ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                super.onActivityCreated(activity, savedInstanceState)
                if (savedInstanceState != null && activity is ComponentActivity) {
                    activity.contentView().post {
                        val composeViewModel = ComposeViewModel.getInstance(activity.viewModelStore)
                        composeViewModel.getAllDestinationDataFlows().forEach {
                            it.value?.let {
                                navigateDirectly(activity, it)
                            }
                        }
                    }
                }
            }
        })
    }

    override fun popBack(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {
        if (activity !is ComponentActivity) {
            "Compose pop back failed! Need ComponentActivity, current activity is: $activity".logd()
            return
        }

        // Clear the ViewModel for the pop back page
        ComposeViewModel.getInstance(activity.viewModelStore)
            .clear(topEntry.destinationData.uniqueTag)

        // Clear the view content for the pop back page
        ComposeNavigatorHelper.clearComposeView(activity, topEntry.destinationData)

        with(activity) {
            val newTopEntry = backStackEntryManager.getTopEntry(this)

            if (newTopEntry != null && isComposeEntry(newTopEntry)) {
                // If new top entry is compose entry, launch new top entry directly
                navigateDirectly(activity, newTopEntry.destinationData)
            }
        }
    }

    override suspend fun navigate(context: Context, data: DestinationData): Result<Bundle> {
        return if (context is ComponentActivity) {
            navigate(context, data)
            Result.success(Bundle.EMPTY)
        } else {
            "Compose navigation failed! Need ComponentActivity, current activity is: $context".logd()
            Result.failure(IllegalStateException("Invalid activity!"))
        }
    }

    private fun navigateDirectly(activity: ComponentActivity, data: DestinationData) {
        ComposeNavigatorHelper.navigate(activity, data)
    }

    private fun navigate(activity: ComponentActivity, data: DestinationData) {
        if (data.groupId.isNotEmpty()) {
            composeGroupNavigator.navigate(activity, data)
        } else {
            composeBackStackNavigator.navigate(activity, data)
        }
    }
}