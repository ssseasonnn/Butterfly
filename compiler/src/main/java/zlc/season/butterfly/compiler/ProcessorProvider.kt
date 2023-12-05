package zlc.season.butterfly.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import zlc.season.butterfly.annotation.Agile
import zlc.season.butterfly.annotation.Evade
import zlc.season.butterfly.annotation.EvadeImpl
import java.io.File
import kotlin.random.Random

class ProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ButterflySymbolProcessor(environment)
    }
}

private class ButterflySymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val agileMap = mutableMapOf<String, String>()
    private val evadeMap = mutableMapOf<String, String>()
    private val evadeImplMap = mutableMapOf<String, EvadeImplInfo>()

    private val composableList = mutableListOf<ComposableInfo>()

    val sourceFileList = mutableListOf<KSFile>()

    private var packageName = DEFAULT_PACKAGE

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val invalidAnnotated = mutableListOf<KSAnnotated>()

        processAgileSymbols(resolver, invalidAnnotated, sourceFileList)
        processEvadeSymbols(resolver, invalidAnnotated, sourceFileList)
        processEvadeImplSymbols(resolver, invalidAnnotated, sourceFileList)

        return invalidAnnotated
    }

    private fun processAgileSymbols(
        resolver: Resolver,
        invalidAnnotated: MutableList<KSAnnotated>,
        sourcesFile: MutableList<KSFile>
    ) {
        environment.logt("Start process Agile symbols...")
        val agileSymbols = resolver.getSymbolsWithAnnotation(Agile::class.qualifiedName!!)
        environment.logc("find symbols: ${agileSymbols.toList()}")

        val visitor = AgileAnnotationVisitor(environment, agileMap, sourcesFile)
        agileSymbols.toList().forEach {
            if (!it.validate()) {
                invalidAnnotated.add(it)
            } else {
                it.accept(visitor, Unit)
                sourcesFile.add((it as KSClassDeclaration).containingFile!!)
            }
        }

        environment.logc("symbol process result: $agileMap")
        environment.logc("Process Agile symbols end.")
    }

    private fun processEvadeSymbols(
        resolver: Resolver,
        invalidAnnotated: MutableList<KSAnnotated>,
        sourcesFile: MutableList<KSFile>
    ) {
        environment.logt("Start process Evade symbols...")
        val evadeSymbols = resolver.getSymbolsWithAnnotation(Evade::class.qualifiedName!!)
        environment.logc("find symbols: ${evadeSymbols.toList()}")

        val visitor = EvadeAnnotationVisitor(environment, evadeMap, sourcesFile)
        evadeSymbols.toList().forEach {
            if (!it.validate()) {
                invalidAnnotated.add(it)
            } else {
                it.accept(visitor, Unit)
            }
        }

        environment.logc("symbol process result: $evadeMap")
        environment.logc("Process Evade symbols end.")
    }

    private fun processEvadeImplSymbols(
        resolver: Resolver,
        invalidAnnotated: MutableList<KSAnnotated>,
        sourcesFile: MutableList<KSFile>
    ) {
        environment.logt("Start process EvadeImpl symbols...")
        val evadeImplSymbols = resolver.getSymbolsWithAnnotation(EvadeImpl::class.qualifiedName!!)
        environment.logc("find symbols: ${evadeImplSymbols.toList()}")

        val visitor = EvadeImplAnnotationVisitor(environment, evadeImplMap, sourcesFile)
        evadeImplSymbols.toList().forEach {
            if (!it.validate()) {
                invalidAnnotated.add(it)
            } else {
                it.accept(visitor, Unit)
            }
        }

        environment.logc("symbol process result: $evadeImplMap")
        environment.logc("Process EvadeImpl symbols end.")
    }


    override fun finish() {
        super.finish()

        val tempOutputFile = environment.codeGenerator.createNewFile(
            Dependencies(true),
            packageName = packageName,
            fileName = TEMP_FILE_NAME,
        )
        tempOutputFile.close()

        val tempFile = environment.codeGenerator.generatedFile.find { it.name.startsWith(TEMP_FILE_NAME) }
        tempFile?.let {
            val generateDir = it.absolutePath
            val className = getModuleNameNew(generateDir)
            environment.logc("Generate module class: $className")

            val outputFile = environment.codeGenerator.createNewFile(
                Dependencies(true, *sourceFileList.toTypedArray()),
                packageName = packageName,
                fileName = className
            )
            val generator = Generator(packageName, className, agileMap, evadeMap, evadeImplMap)
            val fileString = generator.generate().toString()
            outputFile.write(fileString.toByteArray())
            outputFile.close()
        }
    }

    override fun onError() {
        super.onError()
        environment.logc("process error")
    }
}

private class AgileAnnotationVisitor(
    private val environment: SymbolProcessorEnvironment,
    private val agileMap: MutableMap<String, String>,
    private val sourcesFile: MutableList<KSFile>
) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        environment.logc("process symbol: $classDeclaration")
        val annotations = classDeclaration.annotations.toList()
        annotations.forEach { annotation ->
            val schemeValue = annotation.arguments.find { it.name?.asString() == "scheme" }?.value as? String
            val className = "${classDeclaration.packageName.asString()}.${classDeclaration.simpleName.asString()}"
            schemeValue?.let {
                environment.logc("symbol processed: key=$schemeValue, value=$className")
                agileMap[schemeValue] = className

                // add file to dependency
                sourcesFile.add(classDeclaration.containingFile!!)
            }
        }
    }
}

private class EvadeAnnotationVisitor(
    private val environment: SymbolProcessorEnvironment,
    private val evadeMap: MutableMap<String, String>,
    private val sourcesFile: MutableList<KSFile>
) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind == ClassKind.INTERFACE) {
            environment.logc("process symbol: $classDeclaration")
            val annotations = classDeclaration.annotations.toList()
            annotations.forEach { annotation ->
                val identityValue = annotation.arguments.find { it.name?.asString() == "identity" }?.value as? String ?: ""
                val realKey = identityValue.ifEmpty { classDeclaration.simpleName.asString() }
                val className = "${classDeclaration.packageName.asString()}.${classDeclaration.simpleName.asString()}"
                environment.logc("symbol processed: key=$realKey, value=$className")
                evadeMap[realKey] = className

                // add file to dependency
                sourcesFile.add(classDeclaration.containingFile!!)
            }
        } else {
            environment.logc("Invalid symbol: $classDeclaration. @Evade must be annotated at an interface!")
        }
    }
}

private class EvadeImplAnnotationVisitor(
    private val environment: SymbolProcessorEnvironment,
    private val evadeImplMap: MutableMap<String, EvadeImplInfo>,
    private val sourcesFile: MutableList<KSFile>
) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind == ClassKind.CLASS) {
            environment.logc("process symbol: $classDeclaration")
            val annotations = classDeclaration.annotations.toList()
            annotations.forEach { annotation ->
                val isSingleton = annotation.arguments.find { it.name?.asString() == "singleton" }?.value as? Boolean ?: true
                val identityValue = annotation.arguments.find { it.name?.asString() == "identity" }?.value as? String ?: ""
                val classSimpleName = classDeclaration.simpleName.asString()

                if (identityValue.isEmpty() && !classSimpleName.endsWith("Impl")) {
                    environment.logc("Invalid symbol: $classDeclaration. If your @EvadeImpl class does not provide identity value, then the class name must end with Impl!")
                } else {
                    val realKey = identityValue.ifEmpty {
                        val index = classSimpleName.lastIndexOf("Impl")
                        classSimpleName.substring(0, index)
                    }
                    val className = "${classDeclaration.packageName.asString()}.${classDeclaration.simpleName.asString()}"
                    environment.logc("symbol processed: key=$realKey, value=$className")
                    evadeImplMap[realKey] = EvadeImplInfo(className, isSingleton)

                    // add file to dependency
                    sourcesFile.add(classDeclaration.containingFile!!)
                }
            }
        } else {
            environment.logc("Invalid symbol: $classDeclaration. @EvadeImpl must be annotated at an class!")
        }
    }
}


private fun SymbolProcessorEnvironment.logt(log: String) {
    logger.warn("==== $log")
}

private fun SymbolProcessorEnvironment.logc(log: String) {
    logger.warn("---- $log")
}