package zlc.season.butterfly.compiler

import com.google.auto.service.AutoService
import zlc.season.butterfly.annotation.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.tools.Diagnostic


@AutoService(Processor::class)
class Compiler : AbstractProcessor() {
    private var packageName = ""
    private var className = ""

    private val agileMap = mutableMapOf<String, String>()
    private val evadeMap = mutableMapOf<String, String>()
    private val evadeImplMap = mutableMapOf<String, EvadeImplInfo>()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        if (kaptKotlinGeneratedDir != null) {
            className = getModuleName(kaptKotlinGeneratedDir)
            packageName = DEFAULT_PACKAGE
        }
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val types = LinkedHashSet<String>()
        types.add(Agile::class.java.canonicalName)
        types.add(Evade::class.java.canonicalName)
        types.add(EvadeImpl::class.java.canonicalName)
        return types
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val agileElements = roundEnv.getElementsAnnotatedWith(Agile::class.java)
        agileElements.forEach {
            if (it.kind != ElementKind.CLASS) {
                "@Agile must be annotated at Class!".loge()
            } else {
                val annotation = it.getAnnotation(Agile::class.java)
                val scheme = annotation.scheme
                agileMap[scheme] = it.toString()
            }
        }

        val evadeElements = roundEnv.getElementsAnnotatedWith(Evade::class.java)
        evadeElements.forEach {
            if (it.kind != ElementKind.INTERFACE) {
                "@Evade must be annotated at Interface!".loge()
            } else {
                val annotation = it.getAnnotation(Evade::class.java)
                val name = it.simpleName
                val identity = annotation.identity
                val realKey = identity.ifEmpty { name }.toString()
                evadeMap[realKey] = it.toString()
            }
        }

        val evadeImplElements = roundEnv.getElementsAnnotatedWith(EvadeImpl::class.java)
        evadeImplElements.forEach {
            if (it.kind != ElementKind.CLASS) {
                "@EvadeImpl must be annotated at Class!".loge()
            } else {
                val annotation = it.getAnnotation(EvadeImpl::class.java)
                val singleton = annotation.singleton
                val name = it.simpleName
                val identity = annotation.identity
                if (identity.isEmpty() && !name.endsWith("Impl")) {
                    "@EvadeImpl class name must end with Impl!".loge()
                }
                val realKey = identity.ifEmpty {
                    val index = name.lastIndexOf("Impl")
                    name.substring(0, index)
                }.toString()
                evadeImplMap[realKey] = EvadeImplInfo(it.toString(), singleton)
            }
        }

        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        val generator = Generator(packageName, className, agileMap, evadeMap, evadeImplMap)
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