package zlc.season.butterfly.group

import zlc.season.butterfly.AgileRequest

class GroupEntry(val request: AgileRequest) {
    override fun toString(): String {
        return "{scheme=${request.scheme}, class=${request.className}}"
    }
}