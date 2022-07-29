package com.method.intercept.plugin

import org.objectweb.asm.ClassVisitor
import java.io.File


fun createClassVisitor(
    methods: MutableList<Method>,
    logFile: File,
    packagePrefix: String,
    classVisitor: ClassVisitor
): ClassVisitor {
    return InterceptClassVisitor(methods, logFile, packagePrefix, classVisitor)
}