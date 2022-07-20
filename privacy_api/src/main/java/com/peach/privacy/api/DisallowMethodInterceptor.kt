package com.peach.privacy.api

interface DisallowMethodInterceptor {

    fun intercept(context: InvokeContext):Boolean

}

fun i(){

}