package io.github.wandroid.method.intercept

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

fun needTransform(
    className: String,
    packagePrefix: String, blackList: MutableList<String>
): Boolean {
    if (className.startsWith(packagePrefix)) {
        return false
    }
    blackList.forEach {
        if (it.endsWith(".*")) {
            val substring = it.substring(0, it.length - 2)
            if (className.startsWith(substring)) {
                return false
            }
        }
        val lastIndexOf = className.lastIndexOf(".")
        val packageName = className.substring(0, lastIndexOf)
        if (packageName == it) {
            return false
        }
    }
    return true
}