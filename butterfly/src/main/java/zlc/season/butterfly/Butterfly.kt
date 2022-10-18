package zlc.season.butterfly

import android.app.Activity
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

object Butterfly {
    const val RAW_SCHEME = "butterfly_scheme"

    fun agile(scheme: String): AgileRequestHandler {
        val realScheme = parseScheme(scheme)
        val request = ButterflyCore.queryAgile(realScheme).apply {
            val params = parseSchemeParams(scheme)
            bundle.putAll(bundleOf(*params))
        }
        return AgileRequestHandler(request)
    }


    fun retreat(vararg result: Pair<String, Any?>): Boolean {
        return ButterflyCore.dispatchRetreat(Unit, bundleOf(*result))
    }

    fun Activity.retreat(vararg result: Pair<String, Any?>): Boolean {
        return ButterflyCore.dispatchRetreat(this, bundleOf(*result))
    }

    fun Fragment.retreat(vararg result: Pair<String, Any?>): Boolean {
        return ButterflyCore.dispatchRetreat(this, bundleOf(*result))
    }

    fun DialogFragment.retreat(vararg result: Pair<String, Any?>): Boolean {
        return ButterflyCore.dispatchRetreat(this, bundleOf(*result))
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