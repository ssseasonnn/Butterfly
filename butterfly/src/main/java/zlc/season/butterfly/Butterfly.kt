package zlc.season.butterfly

import android.content.Intent
import androidx.core.os.bundleOf
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

object Butterfly {
    private val scope by lazy { MainScope() }
    private val EMPTY_LAMBDA: (Intent) -> Unit = {}

    fun agile(scheme: String): AgileRequest {
        val realScheme = parseScheme(scheme)
        return ButterflyCore.queryAgile(realScheme).apply {
            val params = parseSchemeParams(scheme)
            bundle.putAll(bundleOf(*params))
        }
    }

    fun AgileRequest.params(vararg pair: Pair<String, Any>): AgileRequest {
        return apply {
            bundle.putAll(bundleOf(*pair))
        }
    }

    fun AgileRequest.flow(needResult: Boolean = true): Flow<Result<Intent>> {
        return ButterflyCore.dispatchAgile(this, needResult)
    }

    fun AgileRequest.carry(onError: (Throwable) -> Unit = {}, onResult: (Intent) -> Unit = EMPTY_LAMBDA) {
        if (onResult == EMPTY_LAMBDA) {
            flow(false).launchIn(scope)
        } else {
            flow(true).onEach {
                if (it.isSuccess) {
                    onResult(it.getOrDefault(Intent()))
                } else {
                    onError(it.exceptionOrNull() ?: Throwable())
                }
            }.launchIn(scope)
        }
    }

    val EVADE_LAMBDA: (String, Class<*>) -> Any = { identity, cls ->
        val real = identity.ifEmpty { cls.simpleName }
        var request = ButterflyCore.queryEvade(real)
        if (request.className.isEmpty()) {
            request = request.copy(className = cls.name)
        }
        ButterflyCore.dispatchEvade(request)
    }

    inline fun <reified T> evade(
        identity: String = "",
        noinline func: (String, Class<*>) -> Any = EVADE_LAMBDA
    ): T {
        return func(identity, T::class.java) as T
    }
}