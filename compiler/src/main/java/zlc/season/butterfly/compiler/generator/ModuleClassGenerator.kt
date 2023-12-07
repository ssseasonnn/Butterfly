package zlc.season.butterfly.compiler.generator

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.typeNameOf
import zlc.season.butterfly.annotation.EvadeData
import zlc.season.butterfly.compiler.EvadeImplInfo
import zlc.season.butterfly.module.Module

/**
 * public class ButterflyModuleApp() : Module {
 *  public val destinationMap: HashMap<String, String> = hashMapOf<String, String>()
 *  public val evadeMap: HashMap<String, String> = hashMapOf<String, String>()
 *  public val evadeImplMap: HashMap<String, EvadeData> = hashMapOf<String, EvadeData>()
 *
 *  init {
 *      destinationMap["/path/foo"] = "zlc.season.butterflydemo.MainActivity"
 *      destinationMap["/path/bar"] = "zlc.season.butterflydemo.TestActivity"
 *  }
 *
 *   *  init {
 *      evadeMap["/path/foo"] = "zlc.season.butterflydemo.Foo"
 *      evadeMap["path/bar"] = "zlc.season.butterflydemo.Bar"
 *  }
 *
 *   *  init {
 *      evadeImplMap["/path/foo"] = EvadeData("zlc.season.butterflydemo.Foo",false)
 *      evadeImplMap["/path/bar"] = EvadeData("zlc.season.butterflydemo.Bar",false)
 *  }
 *
 *  public override fun getDestination(): HashMap<String, String> = destinationMap
 *  public override fun getEvade(): HashMap<String, String> = evadeMap
 *  public override fun getEvadeImpl(): HashMap<String, EvadeData> = evadeImplMap
 * }
 */
internal class ModuleClassGenerator(
    private val packageName: String,
    private val className: String,
    private val destinationMap: Map<String, String>,
    private val evadeMap: Map<String, String>,
    private val evadeImplMap: Map<String, EvadeImplInfo>,
) {
    private val moduleClass = Module::class.asClassName()

    private val mapClass = typeNameOf<HashMap<String, Class<*>>>()
    private val mapDataClass = HashMap::class.asClassName().parameterizedBy(String::class.asClassName(), EvadeData::class.asClassName())

    fun generate(): FileSpec {
        val companion = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder("doNothing")
                    .returns(moduleClass)
                    .addStatement("return ${className}()")
                    .build()
            )
            .build()
        val classBuilder = TypeSpec.classBuilder(className)
            .addSuperinterface(moduleClass)
            .primaryConstructor(FunSpec.constructorBuilder().build())
            .addProperty(
                PropertySpec.builder("destinationMap", mapClass)
                    .initializer("hashMapOf<String,  Class<*>>()")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("evadeMap", mapClass)
                    .initializer("hashMapOf<String, Class<*>>()")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("evadeImplMap", mapDataClass)
                    .initializer("hashMapOf<String, EvadeData>()")
                    .build()
            )
            .addInitializerBlock(
                generateDestinationMapBlock()
            )
            .addInitializerBlock(
                generateEvadeMapBlock()
            )
            .addInitializerBlock(
                generateEvadeImplMapBlock()
            )
            .addFunction(
                FunSpec.builder("getDestination")
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("return destinationMap")
                    .returns(mapClass)
                    .build()
            )
            .addFunction(
                FunSpec.builder("getEvade")
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("return evadeMap")
                    .returns(mapClass)
                    .build()
            )
            .addFunction(
                FunSpec.builder("getEvadeImpl")
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("return evadeImplMap")
                    .returns(mapDataClass)
                    .build()
            )
            .addType(companion)

        return FileSpec.builder(packageName, className)
            .addType(classBuilder.build())
            .build()
    }

    private fun generateDestinationMapBlock(): CodeBlock {
        val builder = CodeBlock.Builder()
        destinationMap.forEach { (k, v) ->
            builder.addStatement("""destinationMap["$k"] = ${v}::class.java """)
        }
        return builder.build()
    }

    private fun generateEvadeMapBlock(): CodeBlock {
        val builder = CodeBlock.Builder()
        evadeMap.forEach { (k, v) ->
            builder.addStatement("""evadeMap["$k"] = ${v}::class.java """)
        }
        return builder.build()
    }

    private fun generateEvadeImplMapBlock(): CodeBlock {
        val builder = CodeBlock.Builder()
        evadeImplMap.forEach { (k, v) ->
            builder.addStatement("""evadeImplMap["$k"] = EvadeData(cls=${(v.className)}::class.java, singleton=${v.singleton}) """)
        }
        return builder.build()
    }
}