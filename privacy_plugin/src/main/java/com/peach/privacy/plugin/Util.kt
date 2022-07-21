package com.peach.privacy.plugin


internal fun findByOwnerClass(methods: MutableList<Method>, ownerClassName: String?): Method? {
    if (ownerClassName.isNullOrBlank()) return null
    val filter = methods.filter { it.ownerClassName == ownerClassName }
    return when (filter.size) {
        0 -> {
            null
        }
        1 -> {
            filter.first()
        }
        else -> {
            throw IllegalStateException("PrivacyApi contain more than 2 same ownerClassName.")
        }
    }
}

