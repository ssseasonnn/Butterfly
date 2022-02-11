package zlc.season.butterfly.gradle_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ButterflyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate() {
            val preBuild = project.tasks.getByName("preBuild")
            preBuild.doFirst {
                println("hookPreReleaseBuild")
            }
            preBuild.doLast {
                println("hookPreReleaseBuild")
            }
        }
    }
}