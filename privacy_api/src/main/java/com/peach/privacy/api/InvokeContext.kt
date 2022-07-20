package com.peach.privacy.api

data class InvokeContext(
    val source: String?,
    val className: String,
    val lineNumber: Int,
    val invokeMethod: InvokeMethod
)

data class InvokeMethod(
    val opcode: Int,
    val ownerClassName: String?,
    val methodName: String?,
    val descriptor: String?,
    val isInterface: Boolean
)