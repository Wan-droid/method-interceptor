package com.method.intercept.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.nio.charset.Charset

class PrivacyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        val extension =
            project.extensions.create("methodIntercept", MethodInterceptExtension::class.java)
        val file = project.file(INTERCEPT_FILE_NAME)

        val logsDir = File(project.buildDir, "outputs/logs")
        androidComponents.onVariants {
            val flavorName = it.flavorName
            val buildType = it.buildType
            val child = if (flavorName.isNullOrEmpty()) buildType else "$buildType/$flavorName"
            val outputDir = if (child.isNullOrEmpty()) logsDir else File(logsDir, child)
            it.instrumentation.transformClassesWith(
                InterceptorClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) { param ->
                param.configFile.set(file)
                val outputLogFile = File(outputDir, RECORD_FILE_NAME)
                if (outputLogFile.exists()) {
                    if (outputLogFile.length() > 0) {
                        outputLogFile.writeText("", Charset.forName("utf-8"))
                    }
                }
                param.outputLogDir.set(outputDir)
                param.blackList.set(extension.blackList)
                param.packagePrefix.set(extension.packagePrefix)
            }

            it.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)

        }
    }
}

