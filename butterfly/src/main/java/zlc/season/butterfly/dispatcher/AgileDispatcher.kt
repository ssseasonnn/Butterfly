package zlc.season.butterfly.dispatcher

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.Action
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.group.GroupEntryManager
import zlc.season.butterfly.internal.ButterflyHelper
import zlc.season.butterfly.internal.logw

class AgileDispatcher {
    companion object {
        private const val COMPOSABLE_CLASS = "zlc.season.butterfly.compose.AgileComposable"
        private const val COMPOSE_DISPATCHER_CLASS = "zlc.season.butterfly.compose.ComposeDispatcher"
    }

    private val dispatcherMaps = LinkedHashMap<Class<*>, InnerDispatcher>()

    private val backStackEntryManager = BackStackEntryManager()
    private val groupEntryManager = GroupEntryManager()

    init {
        dispatcherMaps.apply {
            putAll(
                listOf(
                    Action::class.java to ActionDispatcher,
                    FragmentActivity::class.java to ActivityDispatcher(backStackEntryManager),
                    DialogFragment::class.java to DialogFragmentDispatcher(backStackEntryManager),
                    Fragment::class.java to FragmentDispatcher(backStackEntryManager, groupEntryManager)
                )
            )
            try {
                val agileComposableCls = Class.forName(COMPOSABLE_CLASS)
                val composeDispatcherCls = Class.forName(COMPOSE_DISPATCHER_CLASS)
                val composableDispatcher = composeDispatcherCls.getConstructor(
                    BackStackEntryManager::class.java,
                    GroupEntryManager::class.java
                ).newInstance(backStackEntryManager, groupEntryManager) as InnerDispatcher
                put(agileComposableCls, composableDispatcher)
            } catch (e: Exception) {
                e.logw()
            }

            put(Any::class.java, NoneDispatcher)
        }
    }

    suspend fun dispatch(context: Context, request: AgileRequest): Flow<Result<Bundle>> {
        if (request.className.isEmpty()) {
            "Agile --> dispatch failed! Class not found!".logw()
            return flowOf(Result.failure(IllegalStateException("Agile class not found!")))
        }

        return findDispatcher(request).dispatch(context, request)
    }

    fun retreat(bundle: Bundle): AgileRequest? {
        val currentActivity = ButterflyHelper.activity
        if (currentActivity == null) {
            "Agile --> retreat failed! Activity not found".logw()
            return null
        }

        val topEntry = backStackEntryManager.removeTopEntry(currentActivity) ?: return null
        findDispatcher(topEntry.request).retreat(currentActivity, topEntry, bundle)

        return topEntry.request
    }

    private fun findDispatcher(request: AgileRequest): InnerDispatcher {
        val cls = Class.forName(request.className)
        return dispatcherMaps[dispatcherMaps.keys.find { it.isAssignableFrom(cls) }]!!
    }
}