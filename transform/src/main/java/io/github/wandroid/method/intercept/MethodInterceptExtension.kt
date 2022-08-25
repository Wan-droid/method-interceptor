package io.github.wandroid.method.intercept


const val DEFAULT_INTERCEPTOR_CLASS_PREFIX = "intercept"


const val RECORD_FILE_NAME = "intercept_record.txt"


const val INTERCEPT_FILE_NAME = "method_intercept.yaml"


open class MethodInterceptExtension {
    var packagePrefix: String = DEFAULT_INTERCEPTOR_CLASS_PREFIX

    var blackList: MutableList<String> = mutableListOf()
}
