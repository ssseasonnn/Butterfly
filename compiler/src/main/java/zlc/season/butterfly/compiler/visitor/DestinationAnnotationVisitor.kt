package zlc.season.butterfly.compiler.visitor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import zlc.season.butterfly.compiler.ComposeDestinationInfo
import zlc.season.butterfly.compiler.logc
import zlc.season.butterfly.compiler.loge
import zlc.season.butterfly.compiler.utils.BUNDLE_CLASS_NAME
import zlc.season.butterfly.compiler.utils.DESTINATION_NAME
import zlc.season.butterfly.compiler.utils.DESTINATION_ROUTE_KEY
import zlc.season.butterfly.compiler.utils.VIEW_MODEL_CLASS_NAME
import zlc.season.butterfly.compiler.utils.composeDestinationFullClassName
import zlc.season.butterfly.compiler.utils.getAnnotationByName
import zlc.season.butterfly.compiler.utils.getClassFullName
import zlc.season.butterfly.compiler.utils.getValue

class DestinationAnnotationVisitor(
    private val environment: SymbolProcessorEnvironment,
    private val resolver: Resolver,
    private val destinationMap: MutableMap<String, String>,
    private val composeList: MutableList<ComposeDestinationInfo>,
    private val sourcesFile: MutableList<KSFile>
) : KSVisitorVoid() {

    private val bundleClassType = resolver.getClassDeclarationByName(BUNDLE_CLASS_NAME)!!.asStarProjectedType()
    private val viewModelClassType = resolver.getClassDeclarationByName(VIEW_MODEL_CLASS_NAME)!!.asStarProjectedType()

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        environment.logc("process destination: $classDeclaration")
        val annotation = classDeclaration.getAnnotationByName(DESTINATION_NAME)
        if (annotation != null) {
            val schemeValue = annotation.getValue(DESTINATION_ROUTE_KEY, "")
            val className = classDeclaration.getClassFullName()
            if (schemeValue.isNotEmpty()) {
                environment.logc("destination processed: [scheme='$schemeValue', target='$className']")
                destinationMap[schemeValue] = className

                // add file to dependency
                sourcesFile.add(classDeclaration.containingFile!!)
            } else {
                environment.loge("[$classDeclaration] scheme not found!")
            }
        }
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        environment.logc("process compose destination: $function")
        val annotation = function.getAnnotationByName(DESTINATION_NAME)
        if (annotation != null) {
            val packageName = function.packageName.asString()
            val methodName = function.simpleName.asString()
            val schemeValue = annotation.getValue(DESTINATION_ROUTE_KEY, "")
            if (schemeValue.isNotEmpty()) {
                if (function.parameters.isNotEmpty()) {
                    when (function.parameters.size) {
                        1 -> {
                            val parameterKsType = function.parameters[0].type.resolve()
                            if (bundleClassType.isAssignableFrom(parameterKsType)) {
                                composeList.add(ComposeDestinationInfo(packageName, methodName, true, ""))
                            } else if (viewModelClassType.isAssignableFrom(parameterKsType)) {
                                val viewModelClassName = parameterKsType.getClassFullName()
                                composeList.add(ComposeDestinationInfo(packageName, methodName, false, viewModelClassName))
                            } else {
                                environment.loge("[$function] invalid parameter! Compose only support Bundle or ViewModel type!")
                            }
                        }

                        2 -> {
                            val firstParameterKsType = function.parameters[0].type.resolve()
                            val secondParameterKsType = function.parameters[1].type.resolve()
                            val isBundleFirst = bundleClassType.isAssignableFrom(firstParameterKsType)
                            val isViewModelSecond = viewModelClassType.isAssignableFrom(secondParameterKsType)

                            if (isBundleFirst && isViewModelSecond) {
                                val viewModelClassName = secondParameterKsType.getClassFullName()
                                composeList.add(ComposeDestinationInfo(packageName, methodName, true, viewModelClassName))
                            } else {
                                if (!isBundleFirst) {
                                    environment.loge("[$function] first parameter type must be Bundle!")
                                } else {
                                    environment.loge("[$function] second parameter type must be ViewModel!")
                                }
                            }
                        }

                        else -> {
                            environment.loge("[$function] invalid parameter size! Compose only support max 2 parameters!")
                        }
                    }
                } else {
                    composeList.add(ComposeDestinationInfo(packageName, methodName, false, ""))
                }

                val targetClassName = composeDestinationFullClassName(methodName)
                environment.logc("compose destination processed: [scheme='$schemeValue', target='$targetClassName']")
                destinationMap[schemeValue] = targetClassName

                // add file to dependency
                sourcesFile.add(function.containingFile!!)
            } else {
                environment.loge("[$function] scheme not found!")
            }
        }
    }
}