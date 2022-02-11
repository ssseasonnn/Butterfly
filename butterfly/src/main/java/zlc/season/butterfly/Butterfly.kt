package zlc.season.butterfly

import zlc.season.butterfly.ButterflyCore.TYPE_AGILE
import zlc.season.butterfly.ButterflyCore.TYPE_EVADE

class Butterfly private constructor(val scheme: String, val type: Int) {
    companion object {
        fun agile(scheme: String): Butterfly {
            return Butterfly(scheme, TYPE_AGILE)
        }

        fun evade(scheme: String): Butterfly {
            return Butterfly(scheme, TYPE_EVADE)
        }
    }

    suspend fun start() {
        if (type == TYPE_AGILE) {
            val className = ButterflyCore.queryAgile(scheme)
            val request = ButterflyRequest.AgileRequest(scheme, className)
            ButterflyDispatcher.dispatch(request)
        } else {
            "Evade should use get.".logd()
        }
    }

    suspend fun get(): Any {
        return if (type == TYPE_AGILE) {
            val className = ButterflyCore.queryAgile(scheme)
            val request = ButterflyRequest.AgileRequest(scheme, className)
            ButterflyDispatcher.dispatch(request)
        } else {
            val (className, implClassName, isSingleton) = ButterflyCore.queryEvade(scheme)
            val request = ButterflyRequest.EvadeRequest(scheme, className, implClassName, isSingleton)
            ButterflyDispatcher.dispatch(request)
        }
    }
}