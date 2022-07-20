package com.peach.privacy.plugin

import com.android.build.api.instrumentation.*
import com.peach.privacy.api.InvokeContext
import com.peach.privacy.api.InvokeMethod
import org.objectweb.asm.*


interface PrivacyParam : InstrumentationParameters


abstract class PrivacyClassVisitorFactory : AsmClassVisitorFactory<PrivacyParam> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val classData = classContext.currentClassData
        return PrivacyClassVisitor(classData, nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData) = true
}

class PrivacyClassVisitor(private val classData: ClassData, classVisitor: ClassVisitor?) :
    ClassVisitor(Opcodes.ASM5, classVisitor) {
    private var curSource: String? = null

    override fun visitSource(source: String?, debug: String?) {
        curSource = source
        super.visitSource(source, debug)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        return MV(classData, curSource, mv)
    }
}

class MV(
    private val classData: ClassData,
    private val source: String?,
    methodVisitor: MethodVisitor
) :
    MethodVisitor(Opcodes.ASM5, methodVisitor) {

    private var curLine = 0

    override fun visitLineNumber(line: Int, start: Label?) {
        super.visitLineNumber(line, start)
        curLine = line
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        val banMethods = DisallowApi.get().findByOwnerClass(owner)
        println("$opcode,$name,$descriptor")
        if (banMethods == null) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        } else {
            mv.visitLdcInsn(source);
            mv.visitVarInsn(Opcodes.ASTORE, 12);
            mv.visitLdcInsn(classData.className);
            mv.visitVarInsn(Opcodes.ASTORE, 13);
            mv.visitIntInsn(opcode);
            mv.visitVarInsn(Opcodes.ISTORE, 14);
            mv.visitIntInsn(curLine);
            mv.visitVarInsn(Opcodes.ISTORE, 15);
            mv.visitLdcInsn(owner);
            mv.visitVarInsn(Opcodes.ASTORE, 16);
            mv.visitLdcInsn(name);
            mv.visitVarInsn(Opcodes.ASTORE, 17);
            mv.visitLdcInsn(descriptor);
            mv.visitVarInsn(Opcodes.ASTORE, 18);
            mv.visitInsn(if (isInterface) Opcodes.ICONST_1 else Opcodes.ICONST_0);
            mv.visitVarInsn(Opcodes.ISTORE, 19);

            mv.visitVarInsn(Opcodes.ALOAD, 12);
            mv.visitVarInsn(Opcodes.ALOAD, 13);
            mv.visitVarInsn(Opcodes.ILOAD, 14);
            mv.visitVarInsn(Opcodes.ILOAD, 15);
            mv.visitVarInsn(Opcodes.ALOAD, 16);
            mv.visitVarInsn(Opcodes.ALOAD, 17);
            mv.visitVarInsn(Opcodes.ALOAD, 18);
            mv.visitVarInsn(Opcodes.ILOAD, 19);

            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "com/peach/privacy/api/Interceptors",
                "INSTANCE",
                "Lcom/peach/privacy/api/Interceptors;"
            )
            mv.visitVarInsn(Opcodes.ALOAD, 11)
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/peach/privacy/api/Interceptors",
                "findByOwnerClassName",
                "(Ljava/lang/String;)Lcom/peach/privacy/api/DisallowMethodInterceptor",
                false
            )
            println("this call privacy api.${source},${classData.className},${curLine},${owner}.${name},${descriptor}")
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        }

    }
}

fun MethodVisitor.visitIntInsn(value: Int) {
    when {
        value == -1 -> {
            visitIntInsn(Opcodes.ICONST_M1, value)
        }
        value == 1 -> {
            visitIntInsn(Opcodes.ICONST_1, value)
        }
        value == 2 -> {
            visitIntInsn(Opcodes.ICONST_2, value)
        }
        value == 3 -> {
            visitIntInsn(Opcodes.ICONST_3, value)
        }
        value == 4 -> {
            visitIntInsn(Opcodes.ICONST_4, value)
        }
        value == 5 -> {
            visitIntInsn(Opcodes.ICONST_5, value)
        }
        (value in -127..128) -> {
            visitIntInsn(Opcodes.BIPUSH, value)
        }
        (value in -32768..32767) -> {
            visitIntInsn(Opcodes.SIPUSH, value)
        }
        else -> {
            visitIntInsn(Opcodes.LDC, value)
        }
    }
}