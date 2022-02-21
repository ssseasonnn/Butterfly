package zlc.season.butterfly.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*


object ModuleHolder {
    val modulesList = mutableSetOf<String>()
}

abstract class ModuleClassVisitorFactory : AsmClassVisitorFactory<ModuleClassVisitorFactory.ModuleInstrumentation> {
    interface ModuleInstrumentation : InstrumentationParameters {

//        @get:Internal
//        var _modulesList: MutableList<String>?

//        @get:Internal
//        var list: Property<MutableList<String>>
    }

//    private val modulesList: MutableList<String>
//        get() {
//            println("Butterfly --> ${hashCode()}")
//            val memoized = parameters.get()._modulesList
//            return if (memoized != null) {
//                memoized
//            } else {
//                val list = mutableListOf<String>()
//                parameters.get()._modulesList = list
//                list
//            }
//            println("Butterfly --> ${modulesList.hashCode()}")
//        }

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return ModuleClassVisitor(nextClassVisitor,ModuleHolder.modulesList)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        if (classData.interfaces.contains("zlc.season.butterfly.annotation.Module")) {
            ModuleHolder.modulesList.add(classData.className)
        }
        if (classData.superClasses.contains("android.app.Application")) {
            println("Butterfly: ---> ${classData.className}")
            return true
        } else {
            return false
        }
    }
}

class ModuleClassVisitor(val nextClassVisitor: ClassVisitor, val moduleList: Set<String>) : ClassVisitor(Opcodes.ASM7, nextClassVisitor) {

    init {
        println("Butterfly --> $moduleList")
    }

    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        println("Butterfly --> visit start $name")
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        println("Butterfly --> visit method $name")
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == "onCreate") {
            mv = ModuleMethodVisitor(mv, moduleList)
        }
        return mv!!
    }

    override fun visitEnd() {
        super.visitEnd()
        println("Butterfly ---> visitEnd")

    }
}

class ModuleMethodVisitor(val methodVisitor: MethodVisitor, val list: Set<String>) : MethodVisitor(Opcodes.ASM7, methodVisitor) {
    override fun visitCode() {
        super.visitCode()
        println("Butterfly --> visit code")

        list.forEach {
            println("Butterfly --> module $it")
            val moduleName = it.replace('.', '/')
            methodVisitor.visitFieldInsn(
                GETSTATIC,
                "zlc/season/butterfly/ButterflyCore",
                "INSTANCE",
                "Lzlc/season/butterfly/ButterflyCore;"
            )
            methodVisitor.visitTypeInsn(NEW, moduleName)
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, moduleName, "<init>", "()V", false)
            methodVisitor.visitTypeInsn(CHECKCAST, "zlc/season/butterfly/annotation/Module")
            methodVisitor.visitMethodInsn(
                INVOKEVIRTUAL,
                "zlc/season/butterfly/ButterflyCore",
                "addModule",
                "(Lzlc/season/butterfly/annotation/Module;)V",
                false
            )
        }
    }


//    override fun visitInsn(opcode: Int) {
//        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN
//            || opcode == Opcodes.ATHROW
//        ) {
//            //方法在返回之前，打印"end"
//            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
//            mv.visitLdcInsn("end")
//            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
//        }
//        mv.visitInsn(opcode)
//    }


}