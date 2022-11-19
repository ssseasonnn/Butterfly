package zlc.season.butterfly

import androidx.core.os.bundleOf

object Butterfly {
    const val AGILE_REQUEST = "butterfly_request"

    fun agile(scheme: String): AgileRequestHandler {
        val realScheme = parseScheme(scheme)
        val request = ButterflyCore.queryAgile(realScheme).apply {
            val params = parseSchemeParams(scheme)
            bundle.putAll(bundleOf(*params))
        }
        return AgileRequestHandler(request)
    }


    fun retreat(vararg result: Pair<String, Any?>): AgileRequest? {
        return ButterflyCore.dispatchRetreat(bundleOf(*result))
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