package zlc.season.butterfly

import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat

data class AgileRequest(
    val scheme: String,
    val className: String,
    val bundle: Bundle = Bundle(),
    val activityConfig: ActivityConfig = ActivityConfig(),
    val fragmentConfig: FragmentConfig = FragmentConfig(),
    val interceptorController: InterceptorController = InterceptorController(),
    val shouldIntercept: Boolean = true,
    val needResult: Boolean = true
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

data class ActivityConfig(
    val enterAnim: Int = 0,
    val exitAnim: Int = 0,
    val activityOptions: ActivityOptionsCompat? = null
)

data class FragmentConfig(
    var isAdd: Boolean = false,
    var addToBackStack: Boolean = false,
    var enterAnim: Int = 0,
    var exitAnim: Int = 0,
    var popEnterAnim: Int = 0,
    var popExitAnim: Int = 0
)