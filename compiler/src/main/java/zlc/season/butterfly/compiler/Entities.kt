package zlc.season.butterfly.compiler

import com.google.devtools.ksp.symbol.KSFile

data class EvadeImplInfo(val className: String, val singleton: Boolean)

data class ComposableInfo(
    val packageName: String,
    val methodName: String,
    val hasBundle: Boolean,
    val viewModelName: String,
    val ksFile: KSFile? = null
)