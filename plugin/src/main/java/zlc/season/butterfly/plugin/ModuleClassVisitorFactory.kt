package zlc.season.butterfly.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import java.util.concurrent.ConcurrentHashMap


/**
 * Save all module name
 */
object ModuleHolder {
    private val modulesMap = ConcurrentHashMap<String, String>()

    fun printCurrentModules() {
        "Current module info ${modulesMap.values}".log()
    }

    fun addModule(moduleName: String) {
        modulesMap[moduleName] = moduleName
        "Found module $moduleName".log()
    }

    fun forEach(block: (String) -> Unit) {
        modulesMap.values.forEach {
            "Auto register module $it".log()
            block(it)
        }
    }

    fun clearModule() {
        "Clear module info...".log()
        modulesMap.clear()
    }
}

abstract class ModuleClassVisitorFactory : AsmClassVisitorFactory<ModuleClassVisitorFactory.ModuleInstrumentation> {
    interface ModuleInstrumentation : InstrumentationParameters {
        @get:Input
        @get:Optional
        val invalidate: Property<Long>
    }

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return ModuleClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        if (classData.interfaces.contains("zlc.season.butterfly.module.Module")) {
            ModuleHolder.addModule(classData.className)
        }

        return if (classData.superClasses.contains("android.app.Application")) {
            "Found application: ${classData.className}".log()
            true
        } else {
            false
        }
    }
}

class ModuleClassVisitor(nextClassVisitor: ClassVisitor) : ClassVisitor(ASM7, nextClassVisitor) {
    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == "onCreate") {
            mv = ModuleMethodVisitor(mv)
        }
        return mv
    }
}

class ModuleMethodVisitor(methodVisitor: MethodVisitor) : MethodVisitor(ASM7, methodVisitor) {
    override fun visitCode() {
        super.visitCode()

        ModuleHolder.forEach {
            mv.visitFieldInsn(GETSTATIC, "zlc/season/butterfly/ButterflyCore", "INSTANCE", "Lzlc/season/butterfly/ButterflyCore;")
            mv.visitLdcInsn(it)
            mv.visitMethodInsn(INVOKEVIRTUAL, "zlc/season/butterfly/ButterflyCore", "addModuleName", "(Ljava/lang/String;)V", false)
        }
    }
}