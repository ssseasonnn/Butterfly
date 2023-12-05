package zlc.season.butterfly.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.LambdaTypeName.Companion.get
import zlc.season.butterfly.compiler.ComposableHelper.composableLambdaType
import zlc.season.butterfly.compiler.ComposableHelper.paramsComposableLambdaType
import zlc.season.butterfly.compiler.ComposableHelper.paramsViewModelComposableLambdaType
import zlc.season.butterfly.compiler.ComposableHelper.viewModelComposableLambdaType
import java.io.File

/**
Generated file:

package zlc.season.butterfly.compose

import android.os.Bundle
import androidx.compose.runtime.Composable
import kotlin.Any
import kotlin.String
import kotlin.Unit
import zlc.season.butterfly.compose.AgileComposable
import zlc.season.compose.dashboard.DashboardScreen

public class DashboardScreenComposable : AgileComposable() {
public override val composable: @Composable (() -> Unit)? =
@Composable {
DashboardScreen()
}

public override val paramsComposable: @Composable ((Bundle) -> Unit)? =
@Composable { bundle ->
DashboardScreen(bundle)
}

public override val viewModelComposable: @Composable ((Any) -> Unit)? =
@Composable { viewModel ->
DashboardScreen(viewModel as zlc.season.compose.dashboard.DashboardViewModel)
}

public override val paramsViewModelComposable: @Composable ((Bundle, Any) -> Unit)? =
@Composable { bundle, viewModel ->
DashboardScreen(
bundle, viewModel as zlc.season.compose.dashboard.DashboardViewModel
)
}

public override val viewModelClass: String = "zlc.season.compose.dashboard.DashboardViewModel"
}
 */
internal object ComposableHelper {
    private val composeAnnotationCls = ClassName("androidx.compose.runtime", "Composable")
    private val composeAnnotation = AnnotationSpec.builder(composeAnnotationCls).build()

    private val bundleCls = ClassName("android.os", "Bundle")
    private val bundleParams = ParameterSpec.unnamed(bundleCls)

    private val anyParams = ParameterSpec.unnamed(Any::class)

    private val unitType = Unit::class.asTypeName()

    val composableLambdaType = get(returnType = unitType).copy(annotations = arrayListOf(composeAnnotation), nullable = true)

    val viewModelComposableLambdaType = get(parameters = listOf(anyParams), returnType = unitType)
        .copy(annotations = arrayListOf(composeAnnotation), nullable = true)

    val paramsComposableLambdaType = get(parameters = listOf(bundleParams), returnType = unitType)
        .copy(annotations = arrayListOf(composeAnnotation), nullable = true)

    val paramsViewModelComposableLambdaType =
        get(parameters = listOf(bundleParams, anyParams), returnType = unitType)
            .copy(annotations = arrayListOf(composeAnnotation), nullable = true)

    val superCls = ClassName("zlc.season.butterfly.compose", "AgileComposable")
}

internal class ComposableGenerator(
    private val composableList: List<ComposableInfo>
) {
    fun generate(file: File) {
        composableList.forEach {
            createFileSpec(it).writeTo(file)
        }
    }

    fun generateNew(codeGenerator: CodeGenerator) {
        composableList.forEach {
            val file = codeGenerator.createNewFile(
                Dependencies(true),
                packageName = COMPOSABLE_PACKAGE_NAME,
                fileName = "${it.methodName}Composable"
            )
            val fileString = createFileSpec(it).toString()
            file.write(fileString.toByteArray())
            file.close()
        }
    }

    private fun createFileSpec(composableInfo: ComposableInfo): FileSpec {
        val classBuilder = TypeSpec.classBuilder("${composableInfo.methodName}Composable")
            .superclass(ComposableHelper.superCls)
            .apply {
                if (composableInfo.hasBundle) {
                    if (composableInfo.viewModelName.isNotEmpty()) {
                        addProperty(
                            PropertySpec.builder("paramsViewModelComposable", paramsViewModelComposableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { bundle, viewModel -> ${composableInfo.methodName}(bundle, viewModel as ${composableInfo.viewModelName}) }""".trimIndent()
                                )
                                .build()
                        )
                        addProperty(
                            PropertySpec.builder("viewModelClass", String::class)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """ "${composableInfo.viewModelName}" """.trimIndent()
                                )
                                .build()
                        )
                    } else {
                        addProperty(
                            PropertySpec.builder("paramsComposable", paramsComposableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { bundle -> ${composableInfo.methodName}(bundle) }""".trimIndent()
                                )
                                .build()
                        )
                    }
                } else {
                    if (composableInfo.viewModelName.isNotEmpty()) {
                        addProperty(
                            PropertySpec.builder("viewModelComposable", viewModelComposableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { viewModel -> ${composableInfo.methodName}(viewModel as ${composableInfo.viewModelName}) }""".trimIndent()
                                )
                                .build()
                        )
                        addProperty(
                            PropertySpec.builder("viewModelClass", String::class)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """ "${composableInfo.viewModelName}" """.trimIndent()
                                )
                                .build()
                        )
                    } else {
                        addProperty(
                            PropertySpec.builder("composable", composableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { ${composableInfo.methodName}() }""".trimIndent()
                                )
                                .build()
                        )
                    }
                }
            }

        return FileSpec.builder(COMPOSABLE_PACKAGE_NAME, "${composableInfo.methodName}Composable")
            .addType(classBuilder.build())
            .addImport(ClassName(composableInfo.packageName, composableInfo.methodName), "")
            .addImport(ComposableHelper.superCls, "")
            .build()
    }
}