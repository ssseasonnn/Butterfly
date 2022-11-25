package zlc.season.butterfly

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import zlc.season.butterfly.internal.createRequestId

@Parcelize
data class AgileRequest(
    val scheme: String,
    val className: String,
    val bundle: Bundle = Bundle(),

    val enterAnim: Int = 0,
    val exitAnim: Int = 0,
    val flags: Int = 0,
    val containerViewId: Int = 0,

    val needResult: Boolean = false,
    val enableBackStack: Boolean = true,
    val enableGlobalInterceptor: Boolean = true,

    val isRoot: Boolean = false,
    val clearTop: Boolean = false,
    val singleTop: Boolean = false,
    val useReplace: Boolean = false,

    val groupId: String = "",
    val uniqueId: String = createRequestId()
) : Parcelable

data class EvadeRequest(
    val identity: String, val className: String, val implClassName: String, val isSingleton: Boolean
) {
    override fun toString(): String {
        return """[identity="$identity", className="$className", implClassName="$implClassName", isSingleton="$isSingleton"]"""
    }
}
