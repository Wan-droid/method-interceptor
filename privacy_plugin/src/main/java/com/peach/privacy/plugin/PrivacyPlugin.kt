package com.peach.privacy.plugin

import com.android.build.api.instrumentation.*
import com.android.build.api.variant.AndroidComponentsExtension
import com.peach.privacy.plugin.PrivacyPlugin.Companion.RECORD_FILE_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset

class PrivacyPlugin : Plugin<Project> {

    companion object {
        private const val INTERCEPT_FILE_NAME = "method_intercept.yaml"
        const val RECORD_FILE_NAME = "intercept_record.txt"
    }

    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

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
            }

            it.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)

        }
    }
}


interface PrivacyParam : InstrumentationParameters {
    @get:Input
    val configFile: Property<File>

    @get:Input
    val outputLogDir: Property<File>
}

abstract class InterceptorClassVisitorFactory : AsmClassVisitorFactory<PrivacyParam> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val param = parameters.get()
        val yaml = Yaml()
        val data = yaml.loadAs(FileInputStream(param.configFile.get()), Intercept::class.java)

        val outputDir = param.outputLogDir.get()
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        val outputLogFile = File(outputDir, RECORD_FILE_NAME)
        if (!outputLogFile.exists()) {
            outputLogFile.createNewFile()
        }
        return InterceptClassVisitor(data.intercept, outputLogFile, nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        if (classData.className.startsWith("intercept")) {
            return false
        }
        return true
    }
}