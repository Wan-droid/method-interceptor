package com.peach.privacy.api

class DefaultInterceptor : DisallowMethodInterceptor {
    override fun intercept(context: InvokeContext): Boolean {
        return false
    }
}