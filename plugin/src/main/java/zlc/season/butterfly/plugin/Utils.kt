package zlc.season.butterfly.plugin

import org.gradle.api.Project
import java.io.File
import java.util.*

internal const val PACKAGE_NAME = "zlc.season.butterfly"
internal const val SOURCE_SET_NAME = "schemeConfig"
internal const val GENERATE_DIR = "/generated/source/schemeConfig/"

internal const val BUTTERFLY_SCHEME_FILE = "ButterflyScheme.kts"

internal fun findRootDir(dir: File): File? {
    return try {
        var rootDir: File? = null
        var parent: File? = dir
        while (parent != null && rootDir == null) {
            if (parent.isRoot()) {
                rootDir = parent
            }
            parent = parent.parentFile
        }
        rootDir
    } catch (e: Exception) {
        null
    }
}

private fun File.isRoot(): Boolean {
    var result = false
    list()?.forEach {
        if (it == Project.GRADLE_PROPERTIES) {
            result = true
            return@forEach
        }
    }
    return result
}

internal fun cleanDirectory(directory: File) {
    val files = directory.listFiles() ?: return
    files.forEach {
        forceDelete(it)
    }
}

private fun forceDelete(file: File) {
    if (file.isDirectory) {
        if (file.exists()) {
            cleanDirectory(file)
            file.delete()
        }
    } else {
        file.delete()
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