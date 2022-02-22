package zlc.season.butterfly.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*


/**
 * Save all module name
 */
object ModuleHolder {
    val modulesList = mutableSetOf<String>()
}

abstract class ModuleClassVisitorFactory : AsmClassVisitorFactory<ModuleClassVisitorFactory.ModuleInstrumentation> {
    interface ModuleInstrumentation : InstrumentationParameters {
    }

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return ModuleClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        if (classData.interfaces.contains("zlc.season.butterfly.annotation.Module")) {
            ModuleHolder.modulesList.add(classData.className)
        }

        return if (classData.superClasses.contains("android.app.Application")) {
            println("Butterfly --> Found application: ${classData.className}")
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
        ModuleHolder.modulesList.forEach {
            println("Butterfly --> Auto register module: $it")

            val moduleName = it.replace('.', '/')
            mv.visitFieldInsn(
                GETSTATIC,
                "zlc/season/butterfly/ButterflyCore",
                "INSTANCE",
                "Lzlc/season/butterfly/ButterflyCore;"
            )
            mv.visitTypeInsn(NEW, moduleName)
            mv.visitInsn(DUP)
            mv.visitMethodInsn(INVOKESPECIAL, moduleName, "<init>", "()V", false)
            mv.visitTypeInsn(CHECKCAST, "zlc/season/butterfly/annotation/Module")
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "zlc/season/butterfly/ButterflyCore",
                "addModule",
                "(Lzlc/season/butterfly/annotation/Module;)V",
                false
            )
        }
    }
}