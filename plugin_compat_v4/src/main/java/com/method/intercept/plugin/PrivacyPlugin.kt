package com.method.intercept.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class PrivacyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AppExtension::class.java)

        val extension = project.extensions.create("methodIntercept", MethodInterceptExtension::class.java)
        val file = project.file(INTERCEPT_FILE_NAME)

        val outputDir = File(project.buildDir, "outputs/logs")

        androidComponents.registerTransform(MethodInterceptTransform(file, outputDir, extension))
    }
}
