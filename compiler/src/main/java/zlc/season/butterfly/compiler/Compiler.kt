package zlc.season.butterfly.compiler

import com.google.auto.service.AutoService
import zlc.season.butterfly.annotation.Butterfly
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.tools.Diagnostic


@AutoService(Processor::class)
class Compiler : AbstractProcessor() {
    companion object {
        const val PATH = "/build/generated/source/kaptKotlin"
        const val DEFAULT_MODULE_NAME = "ButterflyModule"
        const val DEFAULT_PACKAGE = "zlc.season.bufferfly"
    }

    private var packageName = ""
    private var className = ""
    private val schemeMap = mutableMapOf<String, String>()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        println("compiler init")
        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        if (kaptKotlinGeneratedDir != null) {
            val moduleName = getModuleName(kaptKotlinGeneratedDir)
            className = moduleName.ifEmpty { DEFAULT_MODULE_NAME }
            packageName = DEFAULT_PACKAGE
        }
    }

    private fun getModuleName(generateDir: String): String {
        return try {
            val pathIndex = generateDir.lastIndexOf(PATH)
            val subStr = generateDir.substring(0, pathIndex)
            val lastIndex = subStr.lastIndexOf(File.separatorChar)
            val result = subStr.substring(lastIndex + 1)
            DEFAULT_MODULE_NAME + result.capitalize()
        } catch (e: Exception) {
            "Can not found valid module name, use default!".logn()
            ""
        }
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val types = LinkedHashSet<String>()
        types.add(Butterfly::class.java.canonicalName)
        return types
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(Butterfly::class.java)
        elements.forEach {
            if (it.kind != ElementKind.CLASS) {
                "Butterfly must be annotated at Class!".loge()
            } else {
                val annotation = it.getAnnotation(Butterfly::class.java)
                val scheme = annotation.scheme
                schemeMap[scheme] = it.toString()
            }
        }

        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        val generator = Generator(packageName, className, schemeMap)
        if (kaptKotlinGeneratedDir != null) {
            generator.generate().writeTo(File(kaptKotlinGeneratedDir))
        }

        return false
    }

    private fun String.logn() {
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, this)
    }

    private fun String.logw() {
        processingEnv.messager.printMessage(Diagnostic.Kind.WARNING, this)
    }

    private fun String.loge() {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, this)
    }
}