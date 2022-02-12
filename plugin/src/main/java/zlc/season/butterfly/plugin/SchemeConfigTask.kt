package zlc.season.butterfly.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class SchemeConfigTask : DefaultTask() {

    @Internal
    lateinit var projectName: String

    @Internal
    lateinit var genDir: File

    @Internal
    lateinit var rootDir: File

    @TaskAction
    fun taskAction() {
        if (!genDir.exists()) {
            genDir.mkdirs()
        }

        cleanDirectory(genDir)

        val realRootDir = findRootDir(rootDir) ?: return
        val schemeConfigFile = File(realRootDir, BUTTERFLY_SCHEME_FILE)
        if (schemeConfigFile.exists()) {
            val configMap = parseSchemeConfig(schemeConfigFile)

            SchemeConfigGenerator(PACKAGE_NAME, createSchemeConfigFileName(), configMap).generate().writeTo(genDir)
        }
    }

    private fun parseSchemeConfig(schemeConfigFile: File): Map<String, String> {
        val result = mutableMapOf<String, String>()
        schemeConfigFile.bufferedReader().forEachLine {
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

    private fun createSchemeConfigFileName(): String {
        val name = projectName.camelCase()
        return "Butterfly${name}Scheme"
    }
}