package zlc.season.butterfly.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.utils.setDisallowChanges
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceTask
import java.io.File

class ButterflyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            val appExtension = project.extensions.findByType(AppExtension::class.java)
            appExtension?.applicationVariants?.all { variant ->
                configSchemeTask(it, variant)
            }

            val libraryExtension = project.extensions.findByType(LibraryExtension::class.java)
            libraryExtension?.libraryVariants?.all { variant ->
                configSchemeTask(it, variant)
            }
        }


        project.pluginManager.withPlugin("com.android.application") {

            val androidComponentsExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)
            androidComponentsExtension.onVariants { variant ->
                variant.transformClassesWith(ModuleClassVisitorFactory::class.java, InstrumentationScope.ALL) {
                }
                variant.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_ALL_CLASSES)
            }
        }
    }

    private fun configSchemeTask(project: Project, variant: BaseVariant) {
        val outputDir = "${project.buildDir}${GENERATE_DIR}${variant.name}/"
        val schemeProvider = project.tasks.register("generate${variant.name.cap()}SchemeConfig", SchemeConfigTask::class.java) {
            it.genDir = File(outputDir)
            it.rootDir = project.rootDir
            it.projectName = project.name
        }

        variant.registerJavaGeneratingTask(schemeProvider, File(outputDir))

        val kotlinCompileTask = project.tasks.findByName("compile${variant.name.cap()}Kotlin") as? SourceTask
        kotlinCompileTask?.let {
            it.dependsOn(schemeProvider)
            val srcSet = project.objects.sourceDirectorySet(SOURCE_SET_NAME, SOURCE_SET_NAME).srcDir(outputDir)
            it.source(srcSet)
        }
    }
}