package zlc.season.butterfly

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zlc.season.butterfly.core.InterceptorManager
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.interceptor.DefaultInterceptor
import zlc.season.butterfly.interceptor.Interceptor
import zlc.season.butterfly.internal.ButterflyHelper.findActivity
import zlc.season.butterfly.internal.key
import zlc.season.butterfly.internal.logd
import zlc.season.butterfly.internal.parseRoute
import zlc.season.butterfly.internal.parseRouteParams
import zlc.season.butterfly.launcher.DestinationLauncher
import zlc.season.butterfly.launcher.DestinationLauncherManager

class DestinationHandler(
    private val context: Context,
    private var destinationData: DestinationData = DestinationData(),
    private val interceptorManager: InterceptorManager = InterceptorManager()
) {
    companion object {
        private val EMPTY_CALLBACK: (Result<Bundle>) -> Unit = {}
    }

    /**
     * Set params for destination
     */
    fun params(vararg pair: Pair<String, Any?>): DestinationHandler {
        return apply {
            destinationData.bundle.putAll(bundleOf(*pair))
        }
    }

    fun params(bundle: Bundle): DestinationHandler {
        return apply {
            destinationData.bundle.putAll(bundle)
        }
    }

    /**
     * Skip global interceptor for current navigation
     */
    fun skipGlobalInterceptor(): DestinationHandler {
        return configDestinationData {
            copy(enableGlobalInterceptor = false)
        }
    }

    /**
     * Add interceptor for current navigation
     */
    fun addInterceptor(interceptor: Interceptor): DestinationHandler {
        return apply { interceptorManager.addInterceptor(interceptor) }
    }

    fun addInterceptor(interceptor: suspend (Context, DestinationData) -> DestinationData): DestinationHandler {
        return apply {
            interceptorManager.addInterceptor(DefaultInterceptor(interceptor))
        }
    }

    fun container(containerViewId: Int): DestinationHandler {
        return configDestinationData {
            copy(containerViewId = containerViewId)
        }
    }

    fun container(containerViewTag: String): DestinationHandler {
        return configDestinationData {
            copy(containerViewTag = containerViewTag)
        }
    }

    fun tag(uniqueTag: String): DestinationHandler {
        return configDestinationData {
            copy(uniqueTag = uniqueTag)
        }
    }

    fun group(groupId: String): DestinationHandler {
        return configDestinationData {
            copy(groupId = groupId)
        }
    }

    fun clearTop(): DestinationHandler {
        return configDestinationData {
            copy(clearTop = true)
        }
    }

    fun singleTop(): DestinationHandler {
        return configDestinationData {
            copy(singleTop = true)
        }
    }

    fun asRoot(): DestinationHandler {
        return configDestinationData {
            copy(isRoot = true)
        }
    }

    fun disableBackStack(): DestinationHandler {
        return configDestinationData {
            copy(enableBackStack = false)
        }
    }

    fun addFlag(flag: Int): DestinationHandler {
        return configDestinationData {
            copy(flags = flags or flag)
        }
    }

    fun enterAnim(enterAnim: Int): DestinationHandler {
        return configDestinationData {
            copy(enterAnim = enterAnim)
        }
    }

    fun exitAnim(exitAnim: Int): DestinationHandler {
        return configDestinationData {
            copy(exitAnim = exitAnim)
        }
    }

    fun navigate(
        route: String,
        onResult: (Result<Bundle>) -> Unit = EMPTY_CALLBACK
    ) {
        navigate(route, bundleOf(), onResult)
    }

    fun navigate(
        route: String,
        vararg params: Pair<String, Any?>,
        onResult: (Result<Bundle>) -> Unit = EMPTY_CALLBACK
    ) {
        navigate(route, bundleOf(*params), onResult)
    }

    fun navigate(
        route: String,
        params: Bundle,
        onResult: (Result<Bundle>) -> Unit = EMPTY_CALLBACK
    ) {
        if (context is LifecycleOwner) {
            context.lifecycleScope.launch {
                if (onResult != EMPTY_CALLBACK) {
                    onResult(awaitNavigateResult(route, params))
                } else {
                    awaitNavigate(route, params)
                }
            }
        } else {
            "Navigate failed, context is not LifecycleOwner!".logd()
        }
    }

    suspend fun awaitNavigate(route: String, params: Bundle) {
        setupRouteAndParams(route, false, params)

        ButterflyCore.dispatchNavigate(context, destinationData, interceptorManager)
    }

    suspend fun awaitNavigateResult(route: String, params: Bundle): Result<Bundle> {
        setupRouteAndParams(route, true, params)

        return ButterflyCore.dispatchNavigate(context, destinationData, interceptorManager)
    }

    fun popBack(vararg result: Pair<String, Any?>): DestinationData? {
        return popBack(bundleOf(*result))
    }

    fun popBack(result: Bundle): DestinationData? {
        return ButterflyCore.popBack(context, result)
    }

    private suspend fun setupRouteAndParams(
        route: String,
        needResult: Boolean,
        extraParams: Bundle
    ) {
        withContext(Dispatchers.Default) {
            val parsedRoute = parseRoute(route)
            val destinationClassName = ButterflyCore.queryDestination(parsedRoute)
            val params = parseRouteParams(parsedRoute)

            configDestinationData {
                bundle.putAll(bundleOf(*params))
                bundle.putAll(extraParams)

                copy(
                    route = parsedRoute,
                    className = destinationClassName,
                    needResult = needResult
                )
            }
        }
    }

    fun getLauncher(context: Context): DestinationLauncher {
        val agileHandler = configDestinationData { copy(needResult = true) }

        val activity = context.findActivity()
            ?: throw IllegalStateException("No Activity founded!")

        val key = activity.key()
        var launcher = DestinationLauncherManager.getLauncher(key, destinationData.route)
        if (launcher == null) {
            launcher = DestinationLauncher(
                context,
                agileHandler.destinationData,
                agileHandler.interceptorManager
            )
            DestinationLauncherManager.addLauncher(key, launcher)
        }

        return launcher
    }

    private fun configDestinationData(block: DestinationData.() -> DestinationData): DestinationHandler {
        return apply {
            destinationData = destinationData.block()
        }
    }
}

