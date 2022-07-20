package com.peach.privacy.api

object Interceptors {
    private val interceptorClassNames = hashMapOf<String, String>()
    private val interceptors = hashMapOf<String, DisallowMethodInterceptor>()

    var unInterceptHandle: DisallowMethodInterceptor = DefaultInterceptor()

    init {
        inject()
    }

    private fun inject() {
        //impl by plugin
        //interceptorClasses.put("ownerClassName","interceptor class name")
    }

    @Synchronized
    fun findByOwnerClassName(ownerClassName: String?): DisallowMethodInterceptor? {
        if (ownerClassName == null) return null

        val interceptor = interceptors[ownerClassName]
        if (interceptor != null) return interceptor

        val className = interceptorClassNames[ownerClassName]
        if (!className.isNullOrBlank()) {
            val forName = Class.forName(className)
            val constructor = forName.getDeclaredConstructor()
            val newInstance = constructor.newInstance() as DisallowMethodInterceptor
            interceptors[className] = newInstance
            return newInstance
        }

        return unInterceptHandle
    }
}