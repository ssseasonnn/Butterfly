package zlc.season.butterfly

import androidx.core.os.bundleOf
import zlc.season.butterfly.internal.parseScheme
import zlc.season.butterfly.internal.parseSchemeParams

object Butterfly {
    fun enableLog(flag: Boolean) {
        zlc.season.butterfly.internal.enableLog = flag
    }

    fun agile(scheme: String): AgileHandler {
        val realScheme = parseScheme(scheme)
        val request = ButterflyCore.queryAgile(realScheme).apply {
            val params = parseSchemeParams(scheme)
            bundle.putAll(bundleOf(*params))
        }
        return AgileHandler(request)
    }

    fun retreat(vararg result: Pair<String, Any?>): AgileRequest? {
        return ButterflyCore.dispatchRetreat(bundleOf(*result))
    }

    // avoid inline
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