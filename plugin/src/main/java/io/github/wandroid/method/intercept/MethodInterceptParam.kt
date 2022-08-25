package io.github.wandroid.method.intercept

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import java.io.File

interface MethodInterceptParam : InstrumentationParameters {
    @get:Input
    val configFile: Property<File>

    @get:Input
    val outputLogDir: Property<File>

    @get:Input
    val packagePrefix: Property<String>

    @get:Input
    val blackList: ListProperty<String>
}