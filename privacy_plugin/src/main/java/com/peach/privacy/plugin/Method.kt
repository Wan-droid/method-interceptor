package com.peach.privacy.plugin

import java.io.Serializable

data class Method(val ownerClassName: String) :Serializable{

    private val methods = mutableListOf<MethodDescriptor>()

    fun intercept(name: String?, descriptor: String?): Boolean {
        if (name == null) return false
        val none =
            methods.none { it.name == name && (it.descriptor == null || it.descriptor == descriptor) && it.intercept }
        return !none
    }
}

data class MethodDescriptor(
    val name: String,
    val descriptor: String? = null,
    val intercept: Boolean = false
)
