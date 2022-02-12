package zlc.season.butterfly.gradle_plugin

import com.squareup.kotlinpoet.*

class SchemeGenerator(val packageName: String, val className: String, val map: Map<String, String>) {
    fun generate(): FileSpec {
        val classBuilder = TypeSpec.objectBuilder("${className}SchemeConfig")

        map.forEach { (t, u) ->
            classBuilder.addProperty(
                PropertySpec.builder(t, String::class.asClassName())
                    .addModifiers(KModifier.CONST)
                    .initializer(u)
                    .build()
            )
        }


        return FileSpec.builder(packageName, "${className}SchemeConfig")
            .addType(classBuilder.build())
            .build()
    }
}