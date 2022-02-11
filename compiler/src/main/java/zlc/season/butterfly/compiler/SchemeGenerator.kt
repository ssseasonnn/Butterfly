package zlc.season.butterfly.compiler

import com.squareup.kotlinpoet.*

class SchemeGenerator(val map: Map<String, String>) {
    fun generate(): FileSpec {
        val classBuilder = TypeSpec.classBuilder("SchemeConfig")

        map.forEach { (t, u) ->
            classBuilder.addProperty(
                PropertySpec.builder(t, String::class.asClassName())
                    .initializer(u)
                    .build()
            )
        }


        return FileSpec.builder(packageName, className)
            .addType(classBuilder.build())
            .build()
    }
}