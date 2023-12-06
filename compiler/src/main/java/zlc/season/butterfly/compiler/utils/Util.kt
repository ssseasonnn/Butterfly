package zlc.season.butterfly.compiler.utils

import java.util.Locale
import java.io.File.separatorChar as s

/**
 * default package name for generate module class.
 */
const val DEFAULT_GENERATE_MODULE_PACKAGE = "zlc.season.butterfly.module"
const val DEFAULT_GENERATE_COMPOSABLE_PACKAGE_NAME = "zlc.season.butterfly.compose"

const val TEMP_FILE_NAME = "Temp"

const val BUNDLE_CLASS_NAME = "android.os.Bundle"
const val VIEW_MODEL_CLASS_NAME = "androidx.lifecycle.ViewModel"

const val AGILE_NAME = "Agile"
const val EVADE_NAME = "Evade"
const val EVADE_IMPL_NAME = "EvadeImpl"

const val AGILE_SCHEME_KEY = "scheme"
const val EVADE_IDENTITY_KEY = "identity"
const val EVADE_SINGLETON_KEY = "singleton"

const val EVADE_IMPL_SUFFIX = "Impl"

/**
 * get generate module class name. eg: ButterflyHomeModule
 */
internal fun getGenerateModuleClassName(generateDir: String): String {
    return try {
        val kspGenDir = "${s}build${s}generated${s}ksp"
        val pathIndex = generateDir.lastIndexOf(kspGenDir)
        val subStr = generateDir.substring(0, pathIndex)
        val lastIndex = subStr.lastIndexOf(s)
        val result = subStr.substring(lastIndex + 1)
        "Butterfly${result.camelCase()}Module"
    } catch (e: Exception) {
        "ButterflyDefaultModule"
    }
}


/**
 * The name of each Composable function Class.
 */
internal fun composableFullClassName(methodName: String): String {
    return "$DEFAULT_GENERATE_COMPOSABLE_PACKAGE_NAME.${methodName}Composable"
}

/**
 * The name of generated Composable class.
 * eg:
 * @Agile
 * @Composable
 * fun Test(){}
 *
 * will generate class:
 * class TestComposable: AgileComposable {}
 */
internal fun composableClassName(methodName: String): String {
    return "${methodName}Composable"
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