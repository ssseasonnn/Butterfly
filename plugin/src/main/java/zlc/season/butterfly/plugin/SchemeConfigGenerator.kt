package zlc.season.butterfly.plugin

import com.squareup.kotlinpoet.*

class SchemeConfigGenerator(
    private val packageName: String,
    private val className: String,
    private val configMap: Map<String, String>
) {
    fun generate(): FileSpec {
        val classBuilder = TypeSpec.objectBuilder(className)

        configMap.forEach { (t, u) ->
            classBuilder.addProperty(
                PropertySpec.builder(t, String::class.asClassName())
                    .addModifiers(KModifier.CONST)
                    .initializer(u)
                    .build()
            )
        }

        return FileSpec.builder(packageName, className)
            .addType(classBuilder.build())
            .build()
    }
}