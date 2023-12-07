package zlc.season.butterfly.compiler.generator

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName.Companion.get
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import zlc.season.butterfly.compiler.ComposeDestinationInfo
import zlc.season.butterfly.compiler.utils.DEFAULT_GENERATE_COMPOSABLE_PACKAGE_NAME
import zlc.season.butterfly.compiler.utils.composeDestinationClassName
import zlc.season.butterfly.compiler.generator.ComposableHelper.composableLambdaType
import zlc.season.butterfly.compiler.generator.ComposableHelper.paramsComposableLambdaType
import zlc.season.butterfly.compiler.generator.ComposableHelper.paramsViewModelComposableLambdaType
import zlc.season.butterfly.compiler.generator.ComposableHelper.viewModelComposableLambdaType
import zlc.season.butterfly.compiler.utils.COMPOSE_DESTINATION_CLASS

/**
Generated file:

package zlc.season.butterfly.compose

import android.os.Bundle
import androidx.compose.runtime.Composable
import kotlin.Any
import kotlin.String
import kotlin.Unit
import zlc.season.butterfly.compose.ComposeDestination
import zlc.season.compose.dashboard.DashboardScreen

public class DashboardScreenComposeDestination : ComposeDestination() {
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

    val superCls = ClassName(DEFAULT_GENERATE_COMPOSABLE_PACKAGE_NAME, COMPOSE_DESTINATION_CLASS)
}

internal class ComposableGenerator {
    fun createFileSpec(composeDestinationInfo: ComposeDestinationInfo): FileSpec {
        val classBuilder = TypeSpec.classBuilder(composeDestinationClassName(composeDestinationInfo.methodName))
            .superclass(ComposableHelper.superCls)
            .apply {
                if (composeDestinationInfo.hasBundle) {
                    if (composeDestinationInfo.viewModelName.isNotEmpty()) {
                        addProperty(
                            PropertySpec.builder("paramsViewModelComposable", paramsViewModelComposableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { bundle, viewModel -> ${composeDestinationInfo.methodName}(bundle, viewModel as ${composeDestinationInfo.viewModelName}) }""".trimIndent()
                                )
                                .build()
                        )
                        addProperty(
                            PropertySpec.builder("viewModelClass", String::class)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """ "${composeDestinationInfo.viewModelName}" """.trimIndent()
                                )
                                .build()
                        )
                    } else {
                        addProperty(
                            PropertySpec.builder("paramsComposable", paramsComposableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { bundle -> ${composeDestinationInfo.methodName}(bundle) }""".trimIndent()
                                )
                                .build()
                        )
                    }
                } else {
                    if (composeDestinationInfo.viewModelName.isNotEmpty()) {
                        addProperty(
                            PropertySpec.builder("viewModelComposable", viewModelComposableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { viewModel -> ${composeDestinationInfo.methodName}(viewModel as ${composeDestinationInfo.viewModelName}) }""".trimIndent()
                                )
                                .build()
                        )
                        addProperty(
                            PropertySpec.builder("viewModelClass", String::class)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """ "${composeDestinationInfo.viewModelName}" """.trimIndent()
                                )
                                .build()
                        )
                    } else {
                        addProperty(
                            PropertySpec.builder("composable", composableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { ${composeDestinationInfo.methodName}() }""".trimIndent()
                                )
                                .build()
                        )
                    }
                }
            }

        return FileSpec.builder(DEFAULT_GENERATE_COMPOSABLE_PACKAGE_NAME, composeDestinationClassName(composeDestinationInfo.methodName))
            .addType(classBuilder.build())
            .addImport(ClassName(composeDestinationInfo.packageName, composeDestinationInfo.methodName), "")
            .addImport(ComposableHelper.superCls, "")
            .build()
    }
}