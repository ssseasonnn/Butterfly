package zlc.season.butterfly

import android.os.Bundle

data class AgileRequest(val scheme: String, val className: String, val bundle: Bundle = Bundle())

data class EvadeRequest(val identity: String, val className: String, val implClassName: String, val isSingleton: Boolean) {
    override fun toString(): String {
        return """[identity="$identity", className="$className", implClassName="$implClassName", isSingleton="$isSingleton"]"""
    }
}