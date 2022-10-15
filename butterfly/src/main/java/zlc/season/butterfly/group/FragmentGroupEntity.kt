package zlc.season.butterfly.group

import androidx.fragment.app.Fragment
import zlc.season.butterfly.AgileRequest
import java.lang.ref.WeakReference

class FragmentGroupEntity(
    val request: AgileRequest,
    val reference: WeakReference<Fragment>
)