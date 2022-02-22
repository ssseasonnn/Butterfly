package zlc.season.butterfly

import android.content.Intent
import zlc.season.butterfly.annotation.Module

object Butterfly {
    fun agile(scheme: String): AgileRequest {
        val className = ButterflyCore.queryAgile(scheme)
        return AgileRequest(scheme, className)
    }

    inline fun <reified T> evade(identity: String = ""): T {
        val real = identity.ifEmpty { T::class.java.simpleName }
        val request = ButterflyCore.queryEvade(real)
        return EvadeDispatcher.dispatch(request) as T
    }

    fun AgileRequest.carry(onResult: (Intent) -> Unit = {}): Any {
        return AgileDispatcher.dispatch(this, onResult)
    }
}