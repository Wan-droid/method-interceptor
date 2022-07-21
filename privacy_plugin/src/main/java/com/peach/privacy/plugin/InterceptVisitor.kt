package com.peach.privacy.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


const val INTERCEPTOR_CLASS_SUFFIX = "intercept"


class InterceptClassVisitor(private val methods: MutableList<Method>, classVisitor: ClassVisitor) :
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
        return InterceptMethodVisitor(methods, mv)
    }
}

class InterceptMethodVisitor(
    private val methods: MutableList<Method>,
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
        val banMethods = findByOwnerClass(methods, owner)
        if (banMethods == null || descriptor == null || !banMethods.intercept(name, descriptor)) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        } else {
            println("Intercept handle:$owner.$name $descriptor $isInterface")
            when (opcode) {
                Opcodes.INVOKEVIRTUAL -> {
                    val substring = descriptor.substring(1)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "${INTERCEPTOR_CLASS_SUFFIX}/${owner}",
                        name,
                        "(L$owner;$substring",
                        false
                    )
                }
                Opcodes.INVOKESTATIC -> {
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "${INTERCEPTOR_CLASS_SUFFIX}/${owner}",
                        name,
                        descriptor,
                        false
                    )
                }
            }
        }

    }
}