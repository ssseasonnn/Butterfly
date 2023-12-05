package zlc.season.butterfly.compiler

import com.google.devtools.ksp.symbol.KSFile

internal data class EvadeImplInfo(val className: String, val singleton: Boolean)

internal data class ComposableInfo(
    val packageName: String,
    val methodName: String,
    val hasBundle: Boolean,
    val viewModelName: String,
    val ksFile: KSFile? = null
)