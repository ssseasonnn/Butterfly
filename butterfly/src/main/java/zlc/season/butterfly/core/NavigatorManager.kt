package zlc.season.butterfly.core

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.action.Action
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.internal.logd
import zlc.season.butterfly.internal.logw
import zlc.season.butterfly.navigator.ActionNavigator
import zlc.season.butterfly.navigator.ActivityNavigator
import zlc.season.butterfly.navigator.fragment.DialogFragmentNavigator
import zlc.season.butterfly.navigator.ErrorNavigator
import zlc.season.butterfly.navigator.fragment.FragmentNavigator
import zlc.season.butterfly.navigator.Navigator

class NavigatorManager {
    companion object {
        private const val COMPOSE_DESTINATION_CLASS =
            "zlc.season.butterfly.compose.ComposeDestination"
        private const val COMPOSE_NAVIGATOR_CLASS = "zlc.season.butterfly.compose.ComposeNavigator"
    }

    private val navigatorMaps = LinkedHashMap<Class<*>, Navigator>()

    private val backStackEntryManager = BackStackEntryManager()
    private val groupEntryManager = GroupEntryManager()

    init {
        navigatorMaps.apply {
            putAll(
                listOf(
                    Action::class.java to ActionNavigator,
                    FragmentActivity::class.java to ActivityNavigator(backStackEntryManager),
                    DialogFragment::class.java to DialogFragmentNavigator(backStackEntryManager),
                    Fragment::class.java to FragmentNavigator(
                        backStackEntryManager,
                        groupEntryManager
                    )
                )
            )
            try {
                val composeDestinationCls = Class.forName(COMPOSE_DESTINATION_CLASS)
                val composeNavigatorCls = Class.forName(COMPOSE_NAVIGATOR_CLASS)
                val composeNavigator = composeNavigatorCls.getConstructor(
                    BackStackEntryManager::class.java,
                    GroupEntryManager::class.java
                ).newInstance(backStackEntryManager, groupEntryManager) as Navigator
                put(composeDestinationCls, composeNavigator)
            } catch (e: Exception) {
                e.logw()
            }

            put(Any::class.java, ErrorNavigator)
        }
    }

    suspend fun navigate(context: Context, data: DestinationData): Result<Bundle> {
        if (data.className.isEmpty()) {
            "Navigate failed! Could not find destination class: destination=$data".logd()
            return Result.failure(IllegalStateException("Destination class not found!"))
        }

        return findNavigator(data).navigate(context, data)
    }

    fun popBack(context: Context, result: Bundle): DestinationData? {
        val currentActivity = if (context is Activity) {
            context
        } else {
            "Pop back failed! Need an Activity context: currentContext=$context".logd()
            return null
        }

        val topEntry = backStackEntryManager.removeTopEntry(currentActivity) ?: return null
        findNavigator(topEntry.destinationData).popBack(currentActivity, topEntry, result)

        return topEntry.destinationData
    }

    private fun findNavigator(data: DestinationData): Navigator {
        val cls = Class.forName(data.className)
        return navigatorMaps[navigatorMaps.keys.find { it.isAssignableFrom(cls) }]!!
    }
}