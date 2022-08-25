package io.github.wandroid.method.intercept

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.nio.charset.Charset



internal class InterceptClassVisitor(
    private val methods: MutableList<Method>,
    private val logFile: File,
    private val packagePrefix: String,
    classVisitor: ClassVisitor
) :
    ClassVisitor(Opcodes.ASM6, classVisitor) {
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
        return InterceptMethodVisitor(curSource, methods, logFile, packagePrefix, mv)
    }
}

internal class InterceptMethodVisitor(
    private val source: String?,
    private val methods: MutableList<Method>,
    private val logFile: File,
    private val packagePrefix: String,
    methodVisitor: MethodVisitor
) :
    MethodVisitor(Opcodes.ASM6, methodVisitor) {

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
        val banMethods = owner.findInConfig(methods)
        if (banMethods == null || descriptor == null) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        } else {
            when (banMethods.intercept(name, descriptor)) {
                InterceptState.INTERCEPT -> {
                    val prefix = packagePrefix.replace(".", "/")
                    logFile.appendText(
                        "intercept:$source:$curLine->$owner.$name$descriptor $isInterface\n",
                        Charset.forName("utf-8")
                    )
                    when (opcode) {
                        Opcodes.INVOKEVIRTUAL -> {
                            val substring = descriptor.substring(1)
                            mv.visitMethodInsn(
                                Opcodes.INVOKESTATIC,
                                "${prefix}/${owner}",
                                name,
                                "(L$owner;$substring",
                                false
                            )
                        }
                        Opcodes.INVOKESTATIC -> {
                            mv.visitMethodInsn(
                                Opcodes.INVOKESTATIC,
                                "${prefix}/${owner}",
                                name,
                                descriptor,
                                false
                            )
                        }
                    }
                }
                InterceptState.IGNORE -> {
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                }
                InterceptState.NO_INTERCEPT -> {
                    logFile.appendText(
                        "invoke:$source:$curLine->$owner.$name$descriptor $isInterface\n",
                        Charset.forName("utf-8")
                    )
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                }
            }
        }

    }
}