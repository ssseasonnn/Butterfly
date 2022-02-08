package zlc.season.butterfly.compiler

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

/**
 * public class ButterflyModuleApp() : Module {
 *  public val schemeMap: HashMap<String, String> = hashMapOf<String, String>()
 *  init {
 *      schemeMap["aaa"] = "zlc.season.butterflydemo.MainActivity"
 *      schemeMap["test"] = "zlc.season.butterflydemo.TestActivity"
 *  }
 *
 *  public override fun `get`(): HashMap<String, String> = schemeMap
 * }
 */
class Generator(
    private val packageName: String,
    private val className: String,
    private val schemeMap: Map<String, String>
) {
    private val moduleClass = ClassName("zlc.season.butterfly.annotation", "Module")
    private val mapClass = HashMap::class.asClassName().parameterizedBy(String::class.asClassName(), String::class.asClassName())

    fun generate(): FileSpec {
        val classBuilder = TypeSpec.classBuilder(className)
            .addSuperinterface(moduleClass)
            .addFunction(
                FunSpec.builder("get")
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("return schemeMap")
                    .returns(mapClass)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("schemeMap", mapClass)
                    .initializer("hashMapOf<String, String>()")
                    .build()
            )
            .primaryConstructor(
                generateMapBlock()
            )
        return FileSpec.builder(packageName, className)
            .addType(classBuilder.build())
            .build()
    }

    private fun generateMapBlock(): FunSpec {
        val builder = FunSpec.constructorBuilder()
        schemeMap.forEach { (k, v) ->
            builder.addStatement("""schemeMap["$k"] = "$v" """)
        }
        return builder.build()
    }
}