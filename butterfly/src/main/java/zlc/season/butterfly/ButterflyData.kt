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
    val enableGlobalInterceptor: Boolean = true,
    val needResult: Boolean = true
)

data class EvadeRequest(
    val identity: String, val className: String, val implClassName: String, val isSingleton: Boolean
) {
    override fun toString(): String {
        return """[identity="$identity", className="$className", implClassName="$implClassName", isSingleton="$isSingleton"]"""
    }
}

data class ActivityConfig(
    val enterAnim: Int = 0,
    val exitAnim: Int = 0,
    val activityOptions: ActivityOptionsCompat? = null,
    val flags: Int = 0,
    val clearTop: Boolean = false,
    val singleTop: Boolean = false,
)

data class FragmentConfig(
    val containerViewId: Int = 0,
    val tag: String = "",
    val clearTop: Boolean = false,
    val singleTop: Boolean = false,
    val enableBackStack: Boolean = true,
    val useReplace: Boolean = false,

    val enterAnim: Int = 0,
    val exitAnim: Int = 0,
    val popEnterAnim: Int = 0,
    val popExitAnim: Int = 0
)