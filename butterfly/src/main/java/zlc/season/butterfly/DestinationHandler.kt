package zlc.season.butterfly

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import zlc.season.butterfly.core.InterceptorManager
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.interceptor.DefaultInterceptor
import zlc.season.butterfly.interceptor.Interceptor
import zlc.season.butterfly.internal.ButterflyHelper.findActivity
import zlc.season.butterfly.internal.key
import zlc.season.butterfly.internal.logd
import zlc.season.butterfly.internal.parseRouteScheme
import zlc.season.butterfly.internal.parseRouteSchemeParams
import zlc.season.butterfly.launcher.DestinationLauncher
import zlc.season.butterfly.launcher.DestinationLauncherManager

class DestinationHandler(
    private val context: Context,
    private var destinationData: DestinationData = DestinationData(),
    private val interceptorManager: InterceptorManager = InterceptorManager()
) {
    companion object {
        private const val DEFAULT_GROUP = "butterfly_group"
        private val EMPTY_LAMBDA: (Result<Bundle>) -> Unit = {}
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
     * Skip global interceptor for current navigate.
     */
    fun skipGlobalInterceptor(): DestinationHandler {
        return setupDestinationData {
            copy(enableGlobalInterceptor = false)
        }
    }

    /**
     * Add interceptor for current navigate.
     */
    fun addInterceptor(interceptor: Interceptor): DestinationHandler {
        return apply { interceptorManager.addInterceptor(interceptor) }
    }

    fun addInterceptor(interceptor: suspend (DestinationData) -> DestinationData): DestinationHandler {
        return apply {
            interceptorManager.addInterceptor(DefaultInterceptor(interceptor))
        }
    }

    fun container(containerViewId: Int): DestinationHandler {
        return setupDestinationData {
            copy(containerViewId = containerViewId)
        }
    }

    fun container(containerViewTag: String): DestinationHandler {
        return setupDestinationData {
            copy(containerViewTag = containerViewTag)
        }
    }

    fun tag(uniqueTag: String): DestinationHandler {
        return setupDestinationData {
            copy(uniqueTag = uniqueTag)
        }
    }

    fun group(groupName: String = DEFAULT_GROUP): DestinationHandler {
        return setupDestinationData {
            copy(groupId = groupName)
        }
    }

    fun clearTop(): DestinationHandler {
        return setupDestinationData {
            copy(clearTop = true)
        }
    }

    fun singleTop(): DestinationHandler {
        return setupDestinationData {
            copy(singleTop = true)
        }
    }

    fun asRoot(): DestinationHandler {
        return setupDestinationData {
            copy(isRoot = true)
        }
    }

    fun disableBackStack(): DestinationHandler {
        return setupDestinationData {
            copy(enableBackStack = false)
        }
    }

    fun addFlag(flag: Int): DestinationHandler {
        return setupDestinationData {
            copy(flags = flags or flag)
        }
    }

    fun enterAnim(enterAnim: Int = 0): DestinationHandler {
        return setupDestinationData {
            copy(enterAnim = enterAnim)
        }
    }

    fun exitAnim(exitAnim: Int = 0): DestinationHandler {
        return setupDestinationData {
            copy(exitAnim = exitAnim)
        }
    }

    fun navigate(route: String, onResult: (Result<Bundle>) -> Unit = EMPTY_LAMBDA) {
        if (context is LifecycleOwner) {
            context.lifecycleScope.launch {
                if (onResult != EMPTY_LAMBDA) {
                    val result = awaitNavigateResult(route, bundleOf())
                    onResult(result)
                } else {
                    awaitNavigate(route, bundleOf())
                }
            }
        } else {
            "Navigate failed, context is not LifecycleOwner!".logd()
        }
    }

    fun navigate(
        route: String,
        vararg params: Pair<String, Any?>,
        onResult: (Result<Bundle>) -> Unit = EMPTY_LAMBDA
    ) {
        if (context is LifecycleOwner) {
            context.lifecycleScope.launch {
                if (onResult != EMPTY_LAMBDA) {
                    val result = awaitNavigateResult(route, bundleOf(*params))
                    onResult(result)
                } else {
                    awaitNavigate(route, bundleOf(*params))
                }
            }
        } else {
            "Navigate failed, context is not LifecycleOwner!".logd()
        }
    }

    fun navigate(
        route: String,
        params: Bundle,
        onResult: (Result<Bundle>) -> Unit = EMPTY_LAMBDA
    ) {
        if (context is LifecycleOwner) {
            context.lifecycleScope.launch {
                if (onResult != EMPTY_LAMBDA) {
                    val result = awaitNavigateResult(route, params)
                    onResult(result)
                } else {
                    awaitNavigate(route, params)
                }
            }
        } else {
            "Navigate failed, context is not LifecycleOwner!".logd()
        }
    }

    suspend fun awaitNavigate(route: String, params: Bundle) {
        setupSchemeAndParams(route, false, params)

        ButterflyCore.dispatchDestination(context, destinationData, interceptorManager)
    }

    suspend fun awaitNavigateResult(route: String, params: Bundle): Result<Bundle> {
        setupSchemeAndParams(route, true, params)

        return ButterflyCore.dispatchDestination(context, destinationData, interceptorManager)
    }

    fun popBack(vararg result: Pair<String, Any?>): DestinationData? {
        return popBack(bundleOf(*result))
    }

    fun popBack(result: Bundle): DestinationData? {
        return ButterflyCore.popBack(context, result)
    }

    private fun setupSchemeAndParams(route: String, needResult: Boolean, extraParams: Bundle) {
        val scheme = parseRouteScheme(route)
        val destinationClassName = ButterflyCore.queryDestination(scheme)
        val params = parseRouteSchemeParams(route)

        setupDestinationData {
            bundle.putAll(bundleOf(*params))
            bundle.putAll(extraParams)

            copy(
                scheme = scheme,
                className = destinationClassName,
                needResult = needResult
            )
        }
    }

    fun getLauncher(context: Context): DestinationLauncher {
        val agileHandler = setupDestinationData { copy(needResult = true) }

        val activity = context.findActivity()
            ?: throw IllegalStateException("No Activity founded!")

        val key = activity.key()
        var launcher = DestinationLauncherManager.getLauncher(key, destinationData.scheme)
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

    private fun setupDestinationData(block: DestinationData.() -> DestinationData): DestinationHandler {
        return apply {
            destinationData = destinationData.block()
        }
    }
}

