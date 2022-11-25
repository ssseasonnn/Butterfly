package zlc.season.butterfly

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import zlc.season.butterfly.internal.ButterflyHelper
import zlc.season.butterfly.internal.key

@OptIn(FlowPreview::class)
class AgileHandler(
    var request: AgileRequest,
    val interceptorManager: InterceptorManager = InterceptorManager()
) {
    companion object {
        private const val DEFAULT_GROUP = "butterfly_group"
        private val EMPTY_LAMBDA: (Bundle) -> Unit = {}
    }

    fun params(vararg pair: Pair<String, Any?>): AgileHandler {
        return apply {
            request.bundle.putAll(bundleOf(*pair))
        }
    }

    fun params(bundle: Bundle): AgileHandler {
        return apply { request.bundle.putAll(bundle) }
    }

    fun skipGlobalInterceptor(): AgileHandler {
        return configRequest { copy(enableGlobalInterceptor = false) }
    }

    fun addInterceptor(interceptor: ButterflyInterceptor): AgileHandler {
        return apply { interceptorManager.addInterceptor(interceptor) }
    }

    fun addInterceptor(interceptor: suspend (AgileRequest) -> Unit): AgileHandler {
        return apply {
            interceptorManager.addInterceptor(DefaultButterflyInterceptor(interceptor))
        }
    }

    fun container(containerViewId: Int): AgileHandler {
        return configRequest {
            copy(containerViewId = containerViewId)
        }
    }

    fun group(groupName: String = DEFAULT_GROUP): AgileHandler {
        return configRequest { copy(groupId = groupName) }
    }

    fun clearTop(): AgileHandler {
        return configRequest { copy(clearTop = true) }
    }

    fun singleTop(): AgileHandler {
        return configRequest { copy(singleTop = true) }
    }

    fun asRoot(): AgileHandler {
        return configRequest { copy(isRoot = true) }
    }

    fun disableBackStack(): AgileHandler {
        return configRequest { copy(enableBackStack = false) }
    }

    fun addFlag(flag: Int): AgileHandler {
        return configRequest { copy(flags = flags or flag) }
    }

    fun enterAnim(enterAnim: Int = 0): AgileHandler {
        return configRequest { copy(enterAnim = enterAnim) }
    }

    fun exitAnim(exitAnim: Int = 0): AgileHandler {
        return configRequest { copy(exitAnim = exitAnim) }
    }

    fun flow(): Flow<Unit> {
        val handler = configRequest { copy(needResult = false) }
        return ButterflyCore.dispatchAgile(handler.request, handler.interceptorManager)
            .flatMapConcat { flowOf(Unit) }
    }

    fun resultFlow(): Flow<Result<Bundle>> {
        val handler = configRequest { copy(needResult = true) }
        return ButterflyCore.dispatchAgile(handler.request, handler.interceptorManager)
    }

    fun carry(
        scope: CoroutineScope = ButterflyHelper.scope,
        onError: (Throwable) -> Unit = {},
        onResult: (Bundle) -> Unit = EMPTY_LAMBDA
    ) {
        if (onResult == EMPTY_LAMBDA) {
            flow().launchIn(scope)
        } else {
            resultFlow().onEach {
                if (it.isSuccess) {
                    onResult(it.getOrDefault(Bundle()))
                } else {
                    onError(it.exceptionOrNull() ?: Throwable())
                }
            }.launchIn(scope)
        }
    }

    fun getLauncher(): AgileLauncher {
        val agileHandler = configRequest { copy(needResult = true) }

        val activity = ButterflyHelper.fragmentActivity
            ?: throw IllegalStateException("No FragmentActivity founded!")

        if (activity.lifecycle.currentState != Lifecycle.State.INITIALIZED) {
            throw IllegalStateException("You must call getLauncher() after activity are created.")
        }

        val key = activity.key()
        var launcher = AgileLauncherManager.getLauncher(key, request.scheme)
        if (launcher == null) {
            launcher = AgileLauncher(agileHandler.request, agileHandler.interceptorManager)
            AgileLauncherManager.addLauncher(key, launcher)
        }

        return launcher
    }

    private fun configRequest(block: AgileRequest.() -> AgileRequest): AgileHandler {
        return apply {
            request = request.block()
        }
    }
}

