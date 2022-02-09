package zlc.season.butterfly

data class Request(val scheme: String, val target: String, val type: String = TYPE_ACTIVITY) {
    companion object {
        const val TYPE_ACTIVITY = "activity"
        const val TYPE_FRAGMENT = "fragment"
        const val TYPE_DIALOG_FRAGMENT = "dialog fragment"
        const val TYPE_SERVICE = "service"
    }

    fun isEmpty(): Boolean {
        return scheme.isEmpty() || target.isEmpty()
    }
}