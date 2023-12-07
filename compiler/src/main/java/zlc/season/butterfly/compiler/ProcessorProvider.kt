package zlc.season.butterfly.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import zlc.season.butterfly.annotation.Destination
import zlc.season.butterfly.annotation.Evade
import zlc.season.butterfly.annotation.EvadeImpl
import zlc.season.butterfly.compiler.generator.ComposableGenerator
import zlc.season.butterfly.compiler.generator.ModuleClassGenerator
import zlc.season.butterfly.compiler.utils.BUTTERFLY_LOG_ENABLE
import zlc.season.butterfly.compiler.utils.DEFAULT_GENERATE_COMPOSABLE_PACKAGE_NAME
import zlc.season.butterfly.compiler.utils.DEFAULT_GENERATE_MODULE_PACKAGE
import zlc.season.butterfly.compiler.utils.TEMP_FILE_NAME
import zlc.season.butterfly.compiler.utils.composeDestinationClassName
import zlc.season.butterfly.compiler.utils.getGenerateModuleClassName
import zlc.season.butterfly.compiler.visitor.DestinationAnnotationVisitor
import zlc.season.butterfly.compiler.visitor.EvadeAnnotationVisitor
import zlc.season.butterfly.compiler.visitor.EvadeImplAnnotationVisitor

class ProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        environment.options.forEach {
            environment.logc("options: $it")
        }
        return ButterflySymbolProcessor(environment)
    }
}

private class ButterflySymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val destinationMap = mutableMapOf<String, String>()
    private val evadeMap = mutableMapOf<String, String>()
    private val evadeImplMap = mutableMapOf<String, EvadeImplInfo>()

    private val composableList = mutableListOf<ComposeDestinationInfo>()

    private val sourceFileList = mutableListOf<KSFile>()

    private var packageName = DEFAULT_GENERATE_MODULE_PACKAGE

    override fun process(resolver: Resolver): List<KSAnnotated> {
        processDestinationSymbols(resolver, sourceFileList)
        processEvadeSymbols(resolver, sourceFileList)
        processEvadeImplSymbols(resolver, sourceFileList)

        return emptyList()
    }

    private fun processDestinationSymbols(
        resolver: Resolver,
        sourcesFile: MutableList<KSFile>
    ) {
        environment.logt("Start process Destination symbols...")
        val destinationSymbols = resolver.getSymbolsWithAnnotation(Destination::class.qualifiedName!!)
        environment.logc("find destination list: ${destinationSymbols.toList()}")

        val visitor = DestinationAnnotationVisitor(environment, resolver, destinationMap, composableList, sourcesFile)
        destinationSymbols.toList().forEach {
            it.accept(visitor, Unit)
        }
        environment.logc("process Destination symbols end.")
    }

    private fun processEvadeSymbols(
        resolver: Resolver,
        sourcesFile: MutableList<KSFile>
    ) {
        environment.logt("Start process Evade symbols...")
        val evadeSymbols = resolver.getSymbolsWithAnnotation(Evade::class.qualifiedName!!)
        environment.logc("find evade list: ${evadeSymbols.toList()}")

        val visitor = EvadeAnnotationVisitor(environment, evadeMap, sourcesFile)
        evadeSymbols.toList().forEach {
            it.accept(visitor, Unit)
        }
        environment.logc("process Evade symbols end.")
    }

    private fun processEvadeImplSymbols(
        resolver: Resolver,
        sourcesFile: MutableList<KSFile>
    ) {
        environment.logt("Start process EvadeImpl symbols...")
        val evadeImplSymbols = resolver.getSymbolsWithAnnotation(EvadeImpl::class.qualifiedName!!)
        environment.logc("find evade impl list: ${evadeImplSymbols.toList()}")

        val visitor = EvadeImplAnnotationVisitor(environment, evadeImplMap, sourcesFile)
        evadeImplSymbols.toList().forEach {
            it.accept(visitor, Unit)
        }
        environment.logc("process EvadeImpl symbols end.")
    }

    override fun finish() {
        // create an empty temp file to get current module name
        val tempOutputFile = environment.codeGenerator.createNewFile(
            Dependencies(true),
            packageName = packageName,
            fileName = TEMP_FILE_NAME,
        )
        tempOutputFile.close()

        val tempFile = environment.codeGenerator.generatedFile.find { it.name.startsWith(TEMP_FILE_NAME) }
        tempFile?.let {
            // generate composable class file first.
            if (composableList.isNotEmpty()) {
                environment.logt("Generate composable classes...")
                val composableGenerator = ComposableGenerator()
                composableList.forEach { composableInfo ->
                    val composableClassName = composeDestinationClassName(composableInfo.methodName)
                    environment.logc("generate composable class file: $composableClassName")

                    val composableClassFile = environment.codeGenerator.createNewFile(
                        Dependencies(true),
                        packageName = DEFAULT_GENERATE_COMPOSABLE_PACKAGE_NAME,
                        fileName = composeDestinationClassName(composableInfo.methodName)
                    )
                    val composableClassContent = composableGenerator.createFileSpec(composableInfo).toString()
                    composableClassFile.write(composableClassContent.toByteArray())
                    composableClassFile.close()
                }
            }

            // generate module class file.
            val moduleClassName = getGenerateModuleClassName(it.absolutePath)
            environment.logt("Generate module class file: $moduleClassName")

            val moduleClassFile = environment.codeGenerator.createNewFile(
                Dependencies(true, *sourceFileList.toTypedArray()),
                packageName = packageName,
                fileName = moduleClassName
            )
            val moduleClassGenerator = ModuleClassGenerator(packageName, moduleClassName, destinationMap, evadeMap, evadeImplMap)
            val moduleClassContent = moduleClassGenerator.generate().toString()
            moduleClassFile.write(moduleClassContent.toByteArray())
            moduleClassFile.close()
        }
    }
}

internal fun SymbolProcessorEnvironment.logt(log: String) {
    if (isEnableLog()) {
        logger.warn("==== $log")
    }
}

internal fun SymbolProcessorEnvironment.logc(log: String) {
    if (isEnableLog()) {
        logger.warn("---- $log")
    }
}

internal fun SymbolProcessorEnvironment.loge(log: String) {
    logger.error(log)
}

private fun SymbolProcessorEnvironment.isEnableLog(): Boolean {
    val value = options[BUTTERFLY_LOG_ENABLE]
    return value == "true"
}