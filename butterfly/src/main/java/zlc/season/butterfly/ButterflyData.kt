package zlc.season.butterfly

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AgileRequest(
    val scheme: String,
    val className: String,
    val bundle: Bundle = Bundle(),
    val activityConfig: ActivityConfig = ActivityConfig(),
    val fragmentConfig: FragmentConfig = FragmentConfig(),
    val enableGlobalInterceptor: Boolean = true,
    val needResult: Boolean = true,
    val uniqueId: String = createRequestId()
) : Parcelable

@Parcelize
data class ActivityConfig(
    val enterAnim: Int = 0,
    val exitAnim: Int = 0,
    val flags: Int = 0,
    val clearTop: Boolean = false,
    val singleTop: Boolean = false,
) : Parcelable

@Parcelize
data class FragmentConfig(
    val containerViewId: Int = 0,
    val clearTop: Boolean = false,
    val singleTop: Boolean = false,
    val enableBackStack: Boolean = true,
    val useReplace: Boolean = false,
    val groupId: String = "",
    val enterAnim: Int = 0,
    val exitAnim: Int = 0
) : Parcelable

data class EvadeRequest(
    val identity: String, val className: String, val implClassName: String, val isSingleton: Boolean
) {
    override fun toString(): String {
        return """[identity="$identity", className="$className", implClassName="$implClassName", isSingleton="$isSingleton"]"""
    }
}
