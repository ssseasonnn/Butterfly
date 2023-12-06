package zlc.season.butterfly.compiler.utils

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType

const val BUTTERFLY_LOG_ENABLE = "butterfly.log.enable"

/**
 * Get class full name by KSType.
 * eg: com.example.Test
 */
internal fun KSType.getClassFullName(): String {
    val ksClassDeclaration = declaration as KSClassDeclaration
    val packageName = ksClassDeclaration.packageName.asString()
    val className = ksClassDeclaration.simpleName.asString()
    return "$packageName.$className"
}

/**
 * Get class full name by KSClassDeclaration.
 * eg: com.example.Test
 */
internal fun KSClassDeclaration.getClassFullName(): String {
    return "${packageName.asString()}.${simpleName.asString()}"
}

/**
 * Get annotation's value by key
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> KSAnnotation.getValue(key: String, defaultValue: T): T {
    return arguments.find { it.name?.asString() == key }?.value as? T ?: defaultValue
}

internal fun KSFunctionDeclaration.getAnnotationByName(name: String): KSAnnotation? {
    return annotations.toList().find { it.shortName.asString() == name }
}

internal fun KSClassDeclaration.getAnnotationByName(name: String): KSAnnotation? {
    return annotations.toList().find { it.shortName.asString() == name }
}