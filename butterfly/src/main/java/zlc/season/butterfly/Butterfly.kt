package zlc.season.butterfly

import android.content.Intent

object Butterfly {
    fun agile(scheme: String): AgileRequest {
        return ButterflyCore.queryAgile(scheme)
    }

    fun AgileRequest.carry(onResult: (Result<Intent>) -> Unit = EMPTY_LAMBDA) {
        AgileDispatcher.dispatch(this, onResult)
    }

    inline fun <reified T> evade(identity: String = ""): T {
        val real = identity.ifEmpty { T::class.java.simpleName }
        val request = ButterflyCore.queryEvade(real)
        return EvadeDispatcher.dispatch(request) as T
    }
}