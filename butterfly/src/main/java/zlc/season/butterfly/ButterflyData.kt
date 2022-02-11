package zlc.season.butterfly

import zlc.season.butterfly.annotation.EvadeData

data class Result(val data: Any? = null) {
    fun isSuccess(): Boolean {
        return data != null
    }
}


sealed class ButterflyRequest(val scheme: String, val className: String) {
    class AgileRequest(scheme: String, className: String) : ButterflyRequest(scheme, className)

    class EvadeRequest(scheme: String, className: String, val implClassName: String, val isSingleton: Boolean) : ButterflyRequest(scheme, className)
}