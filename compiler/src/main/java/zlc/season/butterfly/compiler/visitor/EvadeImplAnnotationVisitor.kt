package zlc.season.butterfly.compiler.visitor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitorVoid
import zlc.season.butterfly.compiler.utils.EVADE_IDENTITY_KEY
import zlc.season.butterfly.compiler.utils.EVADE_IMPL_SUFFIX
import zlc.season.butterfly.compiler.utils.EVADE_SINGLETON_KEY
import zlc.season.butterfly.compiler.EvadeImplInfo
import zlc.season.butterfly.compiler.utils.getClassFullName
import zlc.season.butterfly.compiler.utils.getValue
import zlc.season.butterfly.compiler.logc
import zlc.season.butterfly.compiler.loge
import zlc.season.butterfly.compiler.utils.EVADE_IMPL_NAME
import zlc.season.butterfly.compiler.utils.EVADE_NAME
import zlc.season.butterfly.compiler.utils.getAnnotationByName

class EvadeImplAnnotationVisitor(
    private val environment: SymbolProcessorEnvironment,
    private val evadeImplMap: MutableMap<String, EvadeImplInfo>,
    private val sourcesFile: MutableList<KSFile>
) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (classDeclaration.classKind == ClassKind.CLASS) {
            environment.logc("process evade impl: $classDeclaration")
            val annotation = classDeclaration.getAnnotationByName(EVADE_IMPL_NAME)
            if (annotation != null) {
                val isSingleton = annotation.getValue(EVADE_SINGLETON_KEY, true)
                val identityValue = annotation.getValue(EVADE_IDENTITY_KEY, "")
                val classSimpleName = classDeclaration.simpleName.asString()

                if (identityValue.isEmpty() && !classSimpleName.endsWith(EVADE_IMPL_SUFFIX)) {
                    environment.loge("[$classDeclaration] invalid evade impl. If your @EvadeImpl class does not provide identity value, then the class name must end with Impl!")
                } else {
                    val realKey = identityValue.ifEmpty {
                        val index = classSimpleName.lastIndexOf(EVADE_IMPL_SUFFIX)
                        classSimpleName.substring(0, index)
                    }
                    val targetClassName = classDeclaration.getClassFullName()
                    environment.logc("evade impl processed: [identity='$realKey', target='$targetClassName']")
                    evadeImplMap[realKey] = EvadeImplInfo(targetClassName, isSingleton)

                    // add file to dependency
                    sourcesFile.add(classDeclaration.containingFile!!)
                }
            }
        } else {
            environment.loge("[$classDeclaration] invalid evade impl. @EvadeImpl must be annotated at an class!")
        }
    }
}