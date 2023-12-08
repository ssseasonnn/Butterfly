package zlc.season.butterfly.entities

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import zlc.season.butterfly.internal.createDestinationDataTag

@Parcelize
data class DestinationData(
    val scheme: String = "",
    val className: String = "",
    val bundle: Bundle = Bundle(),

    val enterAnim: Int = 0,
    val exitAnim: Int = 0,
    val flags: Int = 0,
    val containerViewId: Int = 0,
    val containerViewTag: String = "",

    val needResult: Boolean = false,
    val enableBackStack: Boolean = true,
    val enableGlobalInterceptor: Boolean = true,

    val isRoot: Boolean = false,
    val clearTop: Boolean = false,
    val singleTop: Boolean = false,
    val useReplace: Boolean = false,

    val groupId: String = "",
    val uniqueTag: String = createDestinationDataTag()
) : Parcelable