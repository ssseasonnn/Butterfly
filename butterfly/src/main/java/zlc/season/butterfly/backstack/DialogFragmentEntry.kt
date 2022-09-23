package zlc.season.butterfly.backstack

import androidx.fragment.app.DialogFragment
import zlc.season.butterfly.AgileRequest
import java.lang.ref.WeakReference

data class DialogFragmentEntry(
    override val request: AgileRequest,
    override val reference: WeakReference<DialogFragment>
) : BaseEntry