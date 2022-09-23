package zlc.season.butterfly.backstack

import androidx.fragment.app.Fragment
import zlc.season.butterfly.AgileRequest
import java.lang.ref.WeakReference

data class FragmentEntry(
    override val request: AgileRequest,
    override val reference: WeakReference<Fragment>
) : BaseEntry