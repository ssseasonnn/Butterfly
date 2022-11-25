package zlc.season.butterfly.backstack

import zlc.season.butterfly.AgileRequest

class BackStackEntry(val request: AgileRequest) {
    override fun toString(): String {
        return "{scheme=${request.scheme}, class=${request.className}}"
    }
}