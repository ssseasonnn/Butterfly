package zlc.season.butterfly.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils.cleanDirectory
import java.io.File

abstract class SchemeTask : DefaultTask() {
    companion object {
        const val ROOT_DIR_FILE_FLAG = "gradle.properties"
        const val BUTTERFLY_SCHEME_FILE = "ButterflyScheme.kts"
    }


    @Internal
    lateinit var genDir: File

    @Internal
    lateinit var rootDir: File

    @TaskAction
    fun taskAction() {
        if (!genDir.exists()) {
            genDir.mkdirs()
        }

//        cleanDirectory(genDir)

        val root = findRootDir(rootDir) ?: return
        val map = parseSchemeFile(root)
        SchemeGenerator("zlc.season.butterfly", "SchemeTest", map).generate().writeTo(genDir)
    }

    fun findRootDir(dir: File): File? {
        try {
            var rootDir: File? = null
            var parent: File? = dir
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
        val result = mutableMapOf<String, String>()

        val file = File(rootDir, BUTTERFLY_SCHEME_FILE)
        file.bufferedReader().forEachLine {
            val line = it.trim()
            if (line.startsWith("val") || line.startsWith("var")) {
                val split = line.split('=')
                if (split.size == 2) {
                    val first = split[0]
                    val second = split[1]
                    val key = first.replace("val", "").replace("var", "").trim()
                    val value = second.trim()
                    result[key] = value
                }
            }
        }

        return result
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

}