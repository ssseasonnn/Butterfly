package zlc.season.butterfly

data class Request(val scheme: String, val target: String, val type: Int = TYPE_ACTIVITY) {
    companion object {
        const val TYPE_ACTIVITY = 0
        const val TYPE_FRAGMENT = 1
        const val TYPE_DIALOG_FRAGMENT = 2
        const val TYPE_SERVICE = 3
    }

    fun isEmpty(): Boolean {
        return scheme.isEmpty() || target.isEmpty()
    }
}