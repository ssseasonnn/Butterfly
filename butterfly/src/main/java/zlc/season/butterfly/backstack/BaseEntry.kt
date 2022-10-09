package zlc.season.butterfly.backstack

import zlc.season.butterfly.AgileRequest
import java.lang.ref.WeakReference

interface BaseEntry {
    val request: AgileRequest
    val reference: WeakReference<*>
}