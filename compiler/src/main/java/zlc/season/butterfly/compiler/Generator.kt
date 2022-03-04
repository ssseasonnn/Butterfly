package zlc.season.butterfly.compiler

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import zlc.season.butterfly.annotation.EvadeData

/**
 * public class ButterflyModuleApp() : Module {
 *  public val agileMap: HashMap<String, String> = hashMapOf<String, String>()
 *  public val evadeMap: HashMap<String, String> = hashMapOf<String, String>()
 *  public val evadeImplMap: HashMap<String, EvadeData> = hashMapOf<String, EvadeData>()
 *
 *  init {
 *      agileMap["/path/foo"] = "zlc.season.butterflydemo.MainActivity"
 *      agileMap["/path/bar"] = "zlc.season.butterflydemo.TestActivity"
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
 *  public override fun getAgile(): HashMap<String, String> = agileMap
 *  public override fun getEvade(): HashMap<String, String> = evadeMap
 *  public override fun getEvadeImpl(): HashMap<String, EvadeData> = evadeImplMap
 * }
 */
@OptIn(ExperimentalStdlibApi::class)
internal class Generator(
    private val packageName: String,
    private val className: String,
    private val agileMap: Map<String, String>,
    private val evadeMap: Map<String, String>,
    private val evadeImplMap: Map<String, EvadeImplInfo>
) {
    private val moduleClass = ClassName("zlc.season.butterfly.annotation", "Module")

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
                PropertySpec.builder("agileMap", mapClass)
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
                generateAgileMapBlock()
            )
            .addInitializerBlock(
                generateEvadeMapBlock()
            )
            .addInitializerBlock(
                generateEvadeImplMapBlock()
            )
            .addFunction(
                FunSpec.builder("getAgile")
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("return agileMap")
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

    private fun generateAgileMapBlock(): CodeBlock {
        val builder = CodeBlock.Builder()
        agileMap.forEach { (k, v) ->
            builder.addStatement("""agileMap["$k"] = ${v}::class.java """)
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