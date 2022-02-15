package zlc.season.butterfly

import android.content.Intent


data class AgileRequest(val scheme: String, val className: String)

data class EvadeRequest(val scheme: String, val className: String, val implClassName: String, val isSingleton: Boolean)