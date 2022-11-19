package zlc.season.butterfly.compiler

import java.util.*
import java.io.File.separatorChar as s

const val DEFAULT_PACKAGE = "zlc.season.butterfly.module"

const val COMPOSABLE_PACKAGE_NAME = "zlc.season.butterfly.compose"

const val BUNDLE_CLASS_NAME = "android.os.Bundle"
const val VIEW_MODEL_CLASS_NAME = "androidx.lifecycle.ViewModel"

internal fun getModuleName(generateDir: String): String {
    return try {
        val kaptGenDir = "${s}build${s}generated${s}source${s}kaptKotlin"
        val pathIndex = generateDir.lastIndexOf(kaptGenDir)
        val subStr = generateDir.substring(0, pathIndex)
        val lastIndex = subStr.lastIndexOf(s)
        val result = subStr.substring(lastIndex + 1)
        "Butterfly${result.camelCase()}Module"
    } catch (e: Exception) {
        "ButterflyDefaultModule"
    }
}

internal fun String.camelCase(): String {
    val words: List<String> = split("[\\W_]+".toRegex())
    val builder = StringBuilder()
    words.forEach {
        val word = if (it.isEmpty()) it else it[0].uppercase() + it.substring(1).lowercase()
        builder.append(word)
    }

    return builder.toString()
}

internal fun String.cap(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

internal fun String.getPackageName(): String {
    return substring(0, lastIndexOf('.'))
}