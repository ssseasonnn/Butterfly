package zlc.season.butterfly

import android.content.Context
import androidx.core.os.bundleOf
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.internal.ButterflyHelper

object Butterfly {
    fun of(context: Context = ButterflyHelper.context): DestinationHandler {
        return DestinationHandler(context)
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