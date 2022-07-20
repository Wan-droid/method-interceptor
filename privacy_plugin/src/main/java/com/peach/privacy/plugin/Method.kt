package com.peach.privacy.plugin

class Method(val ownerClassName: String) {

    private val methods = mutableListOf<MethodDescriptor>()


    fun addBanMethod(descriptor: MethodDescriptor) {
        methods.add(descriptor)
    }

    fun removeBanMethod(descriptor: MethodDescriptor) {
        methods.remove(descriptor)
    }
}

class MethodDescriptor(val name: String, val descriptor: String?=null)

fun Method.dot(methodName: String, descriptor:String? = null) {
    val methodDescriptor = MethodDescriptor(methodName, descriptor)
    addBanMethod(methodDescriptor)

}
