package zlc.season.butterfly.compiler.visitor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitorVoid
import zlc.season.butterfly.compiler.utils.EVADE_IDENTITY_KEY
import zlc.season.butterfly.compiler.utils.getClassFullName
import zlc.season.butterfly.compiler.utils.getValue
import zlc.season.butterfly.compiler.logc
import zlc.season.butterfly.compiler.loge
import zlc.season.butterfly.compiler.utils.EVADE_NAME
import zlc.season.butterfly.compiler.utils.getAnnotationByName

class EvadeAnnotationVisitor(
    private val environment: SymbolProcessorEnvironment,
    private val evadeMap: MutableMap<String, String>,
    private val sourcesFile: MutableList<KSFile>
) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind == ClassKind.INTERFACE) {
            environment.logc("process evade: $classDeclaration")
            val annotation = classDeclaration.getAnnotationByName(EVADE_NAME)
            if (annotation != null) {
                val identityValue = annotation.getValue(EVADE_IDENTITY_KEY, "")
                val realKey = identityValue.ifEmpty { classDeclaration.simpleName.asString() }
                val targetClassName = classDeclaration.getClassFullName()

                environment.logc("evade processed: [identity='$realKey', target='$targetClassName']")
                evadeMap[realKey] = targetClassName

                // add file to dependency
                sourcesFile.add(classDeclaration.containingFile!!)
            }
        } else {
            environment.loge("[$classDeclaration] invalid evade. @Evade must be annotated at an interface!")
        }
    }
}