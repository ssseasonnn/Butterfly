import org.gradle.api.Plugin
import org.gradle.api.Project
import zlc.season.buildlogic.base.setupMaven

class BuildLogicPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.subprojects {
            setupMaven()
        }
    }
}
