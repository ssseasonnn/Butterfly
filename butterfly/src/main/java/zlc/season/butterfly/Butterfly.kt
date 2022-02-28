package zlc.season.butterfly

import android.content.Intent
import androidx.core.os.bundleOf

object Butterfly {
    val EMPTY_LAMBDA: (Result<Intent>) -> Unit = {}

    fun agile(scheme: String): AgileRequest {
        return ButterflyCore.queryAgile(scheme)
    }

    fun AgileRequest.with(pair: Pair<String, Any>): AgileRequest {
        return apply {
            bundle.putAll(bundleOf(pair))
        }
    }

    fun AgileRequest.carry(onResult: (Result<Intent>) -> Unit = EMPTY_LAMBDA) {
        ButterflyCore.dispatchAgile(this, onResult)
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