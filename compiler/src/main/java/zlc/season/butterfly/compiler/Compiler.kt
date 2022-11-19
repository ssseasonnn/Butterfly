package zlc.season.butterfly.compiler

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.asTypeName
import zlc.season.butterfly.annotation.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types
import javax.tools.Diagnostic


@AutoService(Processor::class)
class Compiler : AbstractProcessor() {
    private var packageName = ""
    private var className = ""

    private val agileMap = mutableMapOf<String, String>()
    private val evadeMap = mutableMapOf<String, String>()
    private val evadeImplMap = mutableMapOf<String, EvadeImplInfo>()

    private val composableList = mutableListOf<ComposableInfo>()

    private lateinit var processingEnv: ProcessingEnvironment
    private lateinit var typeUtils: Types
    private lateinit var viewModelType: TypeMirror
    private lateinit var bundleType: TypeMirror

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        this.processingEnv = processingEnv
        this.typeUtils = processingEnv.typeUtils
        this.viewModelType = processingEnv.elementUtils.getTypeElement(VIEW_MODEL_CLASS_NAME).asType()
        this.bundleType = processingEnv.elementUtils.getTypeElement(BUNDLE_CLASS_NAME).asType()

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
        processAgileElements(roundEnv)
        processEvadeElements(roundEnv)
        processEvadeImplElements(roundEnv)

        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        val composableGenerator = ComposableGenerator(composableList)
        if (kaptKotlinGeneratedDir != null) {
            composableGenerator.generate(File(kaptKotlinGeneratedDir))
        }

        val generator = Generator(packageName, className, agileMap, evadeMap, evadeImplMap)
        if (kaptKotlinGeneratedDir != null) {
            generator.generate().writeTo(File(kaptKotlinGeneratedDir))
        }

        return false
    }

    private fun processAgileElements(roundEnv: RoundEnvironment) {
        val agileElements = roundEnv.getElementsAnnotatedWith(Agile::class.java)
        agileElements.forEach {
            processComposeAgile(it)
            processNormalAgile(it)
        }
    }

    private fun processNormalAgile(it: Element) {
        if (it.kind == ElementKind.CLASS) {
            val annotation = it.getAnnotation(Agile::class.java)
            val scheme = annotation.scheme
            agileMap[scheme] = it.toString()
        }
    }

    private fun processComposeAgile(it: Element) {
        if (it.kind == ElementKind.METHOD && it is ExecutableElement) {
            val packageName = it.enclosingElement.toString().getPackageName()
            val methodName = it.simpleName.toString()
            if (it.parameters.isNotEmpty()) {
                when (it.parameters.size) {
                    1 -> {
                        val firstParameterType = (it.parameters[0] as VariableElement).asType()
                        val isViewModel = typeUtils.isSubtype(firstParameterType, viewModelType)
                        val isBundle = typeUtils.isSameType(firstParameterType, bundleType)
                        if (isViewModel) {
                            val viewModelName = firstParameterType.asTypeName().toString()
                            composableList.add(ComposableInfo(packageName, methodName, false, viewModelName))
                        } else if (isBundle) {
                            composableList.add(ComposableInfo(packageName, methodName, true, ""))
                        } else {
                            "AgileComposable parameters only support Bundle or ViewModel type! -> ${it.simpleName}".loge()
                        }
                    }
                    2 -> {
                        val firstParameterType = (it.parameters[0] as VariableElement).asType()
                        val secondParameterType = (it.parameters[1] as VariableElement).asType()
                        val isFirstBundle = typeUtils.isSameType(firstParameterType, bundleType)
                        val isSecondViewModel = typeUtils.isSubtype(secondParameterType, viewModelType)
                        if (isFirstBundle && isSecondViewModel) {
                            val viewModelName = secondParameterType.asTypeName().toString()
                            composableList.add(ComposableInfo(packageName, methodName, true, viewModelName))
                        } else {
                            if (!isFirstBundle) {
                                "AgileComposable first parameters must be Bundle! -> ${it.simpleName}".loge()
                            } else {
                                "AgileComposable second parameters must be ViewModel! -> ${it.simpleName}".loge()
                            }
                        }
                    }
                    else -> {
                        "AgileComposable parameters only support Bundle and ViewModel! -> ${it.simpleName}".loge()
                    }
                }
            } else {
                composableList.add(ComposableInfo(packageName, methodName, false, ""))
            }

            val annotation = it.getAnnotation(Agile::class.java)
            val scheme = annotation.scheme
            agileMap[scheme] = "$COMPOSABLE_PACKAGE_NAME.${it.simpleName}Composable"
        }
    }

    private fun processEvadeImplElements(roundEnv: RoundEnvironment) {
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
    }

    private fun processEvadeElements(roundEnv: RoundEnvironment) {
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