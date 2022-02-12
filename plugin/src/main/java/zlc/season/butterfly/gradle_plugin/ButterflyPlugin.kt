package zlc.season.butterfly.gradle_plugin

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceTask
import java.io.File

class ButterflyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            val appExtension = project.extensions.findByType(AppExtension::class.java)
            appExtension?.applicationVariants?.all { variant ->
                val outputDir = "${project.buildDir}/generated/source/SchemeGenerate/${variant.name}/"
                println("schemetask name:${variant.name + "SchemeGenerate"}")
                val schemeProvider = project.tasks.register("schemeGenerate${variant.name}", SchemeTask::class.java) {
                    it.genDir = File(outputDir)
                    it.rootDir = project.rootDir
                }

                variant.registerJavaGeneratingTask(schemeProvider, File(outputDir))

                val kotlinCompileTask = it.tasks.findByName("kapt${variant.name.capitalize()}Kotlin") as? SourceTask
                if (kotlinCompileTask != null) {
                    kotlinCompileTask.dependsOn(schemeProvider)
                    val srcSet = it.objects.sourceDirectorySet("SchemeGenerate", "SchemeGenerate").srcDir(outputDir)
                    kotlinCompileTask.source(srcSet)
                }
            }

            val libraryExtension = project.extensions.findByType(LibraryExtension::class.java)
            libraryExtension?.libraryVariants?.all { variant ->
                val outputDir = "${project.buildDir}/generated/source/SchemeGenerate/${variant.name}/"
                println("schemetask name:${variant.name + "SchemeGenerate"}")
                val schemeProvider = project.tasks.register("schemeGenerate${variant.name}", SchemeTask::class.java) {
                    it.genDir = File(outputDir)
                    it.rootDir = project.rootDir
                }

                variant.registerJavaGeneratingTask(schemeProvider, File(outputDir))

                val kotlinCompileTask = it.tasks.findByName("compile${variant.name.capitalize()}Kotlin") as? SourceTask
                if (kotlinCompileTask != null) {
                    kotlinCompileTask.dependsOn(schemeProvider)
                    val srcSet = it.objects.sourceDirectorySet("SchemeGenerate", "SchemeGenerate").srcDir(outputDir)
                    kotlinCompileTask.source(srcSet)
                }
            }
        }
    }
}