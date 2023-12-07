package zlc.season.butterfly.compiler

data class EvadeImplInfo(val className: String, val singleton: Boolean)

data class ComposeDestinationInfo(
    val packageName: String,
    val methodName: String,
    val hasBundle: Boolean,
    val viewModelName: String,
)