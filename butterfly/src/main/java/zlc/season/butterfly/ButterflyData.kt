package zlc.season.butterfly

import android.os.Bundle

data class AgileRequest(
    val scheme: String,
    val className: String,
    val bundle: Bundle = Bundle(),
    val fragmentConfig: FragmentConfig = FragmentConfig(),
    val needIntercept: Boolean = true,
    val needResult: Boolean = true
)

data class FragmentConfig(
    val addOrReplace: Boolean = true,
    val addToBackStack: Boolean = true,
    val enterAnim: Int = 0,
    val exitAnim: Int = 0,
    val popEnterAnim: Int = 0,
    val popExitAnim: Int = 0
)

data class EvadeRequest(
    val identity: String,
    val className: String,
    val implClassName: String,
    val isSingleton: Boolean
) {
    override fun toString(): String {
        return """[identity="$identity", className="$className", implClassName="$implClassName", isSingleton="$isSingleton"]"""
    }
}