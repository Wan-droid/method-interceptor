package com.peach.privacy.plugin

class Intercept {
    var intercept = mutableListOf<Method>()
}

class Method {
    lateinit var ownerClassName: String
    var methods = mutableListOf<MethodDescriptor>()
}

class MethodDescriptor {
    lateinit var name: String
    var descriptor: String? = null
    var intercept: Boolean = false
}

fun Method.intercept(name: String?, descriptor: String?): Boolean {
    if (name == null) return false
    val none =
        methods.none { it.name == name && (it.descriptor == null || it.descriptor == descriptor) && it.intercept }
    return !none
}
