package com.method.intercept.plugin

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

internal enum class InterceptState {
    IGNORE, NO_INTERCEPT, INTERCEPT
}

internal fun Method.intercept(name: String?, descriptor: String): InterceptState {
    if (name == null) return InterceptState.IGNORE
    val filter = methods.filter { it.name == name }
    if (filter.isEmpty()) return InterceptState.IGNORE
    val nullDescriptor = filter.filter { it.descriptor == null }
    if (nullDescriptor.isNotEmpty()) {
        if (filter.size > 1) {
            println("Warn:$name , method description is null and contains multiple definitions")
        }
        val noIntercept = nullDescriptor.none { it.intercept }
        return if (noIntercept) InterceptState.NO_INTERCEPT else InterceptState.INTERCEPT
    }
    val descFilter = filter.filter { it.descriptor == descriptor }
    if (descFilter.isEmpty()) return InterceptState.IGNORE
    val noDescIntercept = descFilter.none { it.intercept }
    return if (noDescIntercept) InterceptState.NO_INTERCEPT else InterceptState.INTERCEPT
}

internal fun String?.findInConfig(methods: MutableList<Method>): Method? {
    if (this.isNullOrBlank()) return null
    val filter = methods.filter { it.ownerClassName == this }
    return if (filter.isEmpty()) {
        null
    } else {
        val result = Method()
        result.ownerClassName = this
        filter.forEach {
            result.methods.addAll(it.methods)
        }
        if (result.methods.isEmpty()) {
            println("Warn:$this has no method defined.")
        }
        result
    }
}
