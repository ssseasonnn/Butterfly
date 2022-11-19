package zlc.season.butterfly

import android.os.Bundle
import androidx.core.os.bundleOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class)
class AgileRequestHandler(
    var request: AgileRequest,
    val interceptorController: InterceptorController = InterceptorController()
) {
    companion object {
        private const val DEFAULT_GROUP = "butterfly_group"
        private val EMPTY_LAMBDA: (Bundle) -> Unit = {}
    }

    fun params(vararg pair: Pair<String, Any?>): AgileRequestHandler {
        return apply {
            request.bundle.putAll(bundleOf(*pair))
        }
    }

    fun params(bundle: Bundle): AgileRequestHandler {
        return apply { request.bundle.putAll(bundle) }
    }

    fun skipGlobalInterceptor(): AgileRequestHandler {
        return configRequest { copy(enableGlobalInterceptor = false) }
    }

    fun addInterceptor(interceptor: ButterflyInterceptor): AgileRequestHandler {
        return apply { interceptorController.addInterceptor(interceptor) }
    }

    fun addInterceptor(interceptor: suspend (AgileRequest) -> Unit): AgileRequestHandler {
        return apply {
            interceptorController.addInterceptor(DefaultButterflyInterceptor(interceptor))
        }
    }

    fun container(containerViewId: Int): AgileRequestHandler {
        return configRequest {
            copy(containerViewId = containerViewId)
        }
    }

    fun group(groupName: String = DEFAULT_GROUP): AgileRequestHandler {
        return configRequest { copy(groupId = groupName) }
    }

    fun clearTop(): AgileRequestHandler {
        return configRequest { copy(clearTop = true) }
    }

    fun singleTop(): AgileRequestHandler {
        return configRequest { copy(singleTop = true) }
    }

    fun asRoot(): AgileRequestHandler {
        return configRequest { copy(isRoot = true) }
    }

    fun disableBackStack(): AgileRequestHandler {
        return configRequest { copy(enableBackStack = false) }
    }

    fun addFlag(flag: Int): AgileRequestHandler {
        return configRequest { copy(flags = flags or flag) }
    }

    fun enterAnim(enterAnim: Int = 0): AgileRequestHandler {
        return configRequest { copy(enterAnim = enterAnim) }
    }

    fun exitAnim(exitAnim: Int = 0): AgileRequestHandler {
        return configRequest { copy(exitAnim = exitAnim) }
    }

    fun flow(): Flow<Unit> {
        val handler = configRequest { copy(needResult = false) }
        return ButterflyCore.dispatchAgile(handler).flatMapConcat { flowOf(Unit) }
    }

    fun resultFlow(): Flow<Result<Bundle>> {
        val handler = configRequest { copy(needResult = true) }
        return ButterflyCore.dispatchAgile(handler)
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

    private fun configRequest(block: AgileRequest.() -> AgileRequest): AgileRequestHandler {
        return apply {
            request = request.block()
        }
    }
}