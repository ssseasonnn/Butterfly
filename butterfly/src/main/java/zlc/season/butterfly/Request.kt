package zlc.season.butterfly

data class Request(val scheme: String, val dest: String) {
    fun isEmpty(): Boolean {
        return scheme.isEmpty() || dest.isEmpty()
    }
}