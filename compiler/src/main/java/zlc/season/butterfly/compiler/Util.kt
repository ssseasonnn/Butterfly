package zlc.season.butterfly.compiler

import java.io.File

const val PATH = "/build/generated/source/kaptKotlin"

const val DEFAULT_PACKAGE = "zlc.season.bufferfly"
const val DEFAULT_MODULE_NAME = "ButterflyModule"

const val ROOT_DIR_FILE_FLAG = "gradle.properties"
const val BUTTERFLY_SCHEME_FILE = "ButterflyScheme.kts"

fun getModuleName(generateDir: String): String {
    return try {
        val pathIndex = generateDir.lastIndexOf(PATH)
        val subStr = generateDir.substring(0, pathIndex)
        val lastIndex = subStr.lastIndexOf(File.separatorChar)
        val result = subStr.substring(lastIndex + 1)
        DEFAULT_MODULE_NAME + result.capitalize()
    } catch (e: Exception) {
        ""
    }
}

fun findRootDir(generateDir: String): File? {
    try {
        val pathIndex = generateDir.lastIndexOf(PATH)
        val subStr = generateDir.substring(0, pathIndex)

        val file = File(subStr)
        var rootDir: File? = null
        var parent: File? = file
        while (parent != null && rootDir == null) {
            if (parent.isRoot()) {
                rootDir = parent
            }
            parent = parent.parentFile
        }
        return rootDir
    } catch (e: Exception) {
        return null
    }
}

fun parseSchemeFile(rootDir: File): Map<String, String> {
    val file = File(rootDir, BUTTERFLY_SCHEME_FILE)
    file.bufferedReader().forEachLine {
        it.trim()
    }
}

private fun File.isRoot(): Boolean {
    var result = false
    list()?.forEach {
        if (it == ROOT_DIR_FILE_FLAG) {
            result = true
            return@forEach
        }
    }
    return result
}
