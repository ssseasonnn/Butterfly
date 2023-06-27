package zlc.season.butterfly

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import zlc.season.butterfly.internal.ButterflyHelper.findActivity
import zlc.season.butterfly.internal.ButterflyHelper.findScope
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

    fun addInterceptor(interceptor: suspend (AgileRequest) -> AgileRequest): AgileHandler {
        return apply {
            interceptorManager.addInterceptor(DefaultButterflyInterceptor(interceptor))
        }
    }

    fun container(containerViewId: Int): AgileHandler {
        return configRequest {
            copy(containerViewId = containerViewId)
        }
    }

    fun container(containerViewTag: String): AgileHandler {
        return configRequest {
            copy(containerViewTag = containerViewTag)
        }
    }

    fun tag(uniqueTag: String): AgileHandler {
        return configRequest { copy(uniqueTag = uniqueTag) }
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

    fun flow(context: Context): Flow<Unit> {
        val handler = configRequest { copy(needResult = false) }
        return ButterflyCore.dispatchAgile(context, handler.request, handler.interceptorManager)
            .flatMapConcat {
                if (it.isSuccess) {
                    flowOf(Unit)
                } else {
                    throw it.exceptionOrNull() ?: Throwable()
                }
            }
    }

    fun resultFlow(context: Context): Flow<Result<Bundle>> {
        val handler = configRequest { copy(needResult = true) }
        return ButterflyCore.dispatchAgile(context, handler.request, handler.interceptorManager)
    }

    fun carry(
        context: Context,
        onError: (Throwable) -> Unit = {},
        onSuccess: () -> Unit = {},
        onResult: (Bundle) -> Unit = EMPTY_LAMBDA
    ) {
        if (onResult == EMPTY_LAMBDA) {
            flow(context).catch { onError(it) }
                .onCompletion { onSuccess() }
                .launchIn(context.findScope())
        } else {
            resultFlow(context)
                .onEach {
                    if (it.isSuccess) {
                        onResult(it.getOrDefault(Bundle()))
                    } else {
                        onError(it.exceptionOrNull() ?: Throwable())
                    }
                }
                .catch { onError(it) }
                .onCompletion { onSuccess() }
                .launchIn(context.findScope())
        }
    }

    fun getLauncher(context: Context): AgileLauncher {
        val agileHandler = configRequest { copy(needResult = true) }

        val activity = context.findActivity()
            ?: throw IllegalStateException("No Activity founded!")

        val key = activity.key()
        var launcher = AgileLauncherManager.getLauncher(key, request.scheme)
        if (launcher == null) {
            launcher = AgileLauncher(context, agileHandler.request, agileHandler.interceptorManager)
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

