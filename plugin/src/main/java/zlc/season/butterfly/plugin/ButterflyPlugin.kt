package zlc.season.butterfly.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.android.build.gradle.internal.utils.setDisallowChanges
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.util.Locale

class ButterflyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.withPlugin("com.android.application") {
            val androidComponentsExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)
            androidComponentsExtension.onVariants { variant ->

                val task = project.tasks.register("clean${variant.name.cap()}ButterflyModule", CleanModuleMapTask::class.java) {}
                val cleanTask = project.tasks.named("clean")
                cleanTask.dependsOn(task)

                variant.instrumentation.transformClassesWith(ModuleClassVisitorFactory::class.java, InstrumentationScope.ALL) {
                    it.invalidate.setDisallowChanges(System.currentTimeMillis())
                }
                variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
            }
        }
    }

    private fun String.cap(): String {
        return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

abstract class CleanModuleMapTask : DefaultTask() {
    @TaskAction
    fun action() {
        ModuleHolder.clearModule()
    }
}