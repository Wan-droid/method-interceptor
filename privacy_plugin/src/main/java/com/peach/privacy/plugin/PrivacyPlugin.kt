package com.peach.privacy.plugin

import com.android.build.api.instrumentation.*
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor
import org.yaml.snakeyaml.Yaml
import java.nio.charset.Charset

class PrivacyPlugin : Plugin<Project> {

    companion object {
        private const val INTERCEPT_FILE_NAME = "method_intercept.yaml"
    }

    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        val file = project.file(INTERCEPT_FILE_NAME)
        val readText = file.readText(Charset.forName("utf-8"))

        androidComponents.onVariants {
            it.instrumentation.transformClassesWith(
                InterceptorClassVisitorFactory::class.java,
                InstrumentationScope.PROJECT
            ) { param ->
                param.jsonConfig.set(readText)
            }

            it.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)

        }
    }
}


interface PrivacyParam : InstrumentationParameters {
    @get:Input
    val jsonConfig: Property<String>
}

abstract class InterceptorClassVisitorFactory : AsmClassVisitorFactory<PrivacyParam> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val param = parameters.get()
        val yaml = Yaml()
        val data = yaml.loadAs(param.jsonConfig.get(), Intercept::class.java)

        return InterceptClassVisitor(data.intercept, nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        if (classData.className.startsWith("intercept")) {
            return false
        }
        return true
    }
}