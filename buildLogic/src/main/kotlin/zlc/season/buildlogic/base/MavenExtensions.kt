package zlc.season.buildlogic.base

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

fun Project.setupMaven() {
    afterEvaluate {
        plugins.withId("com.android.library") {
            configPublish("release")
        }
        plugins.withId("java") {
            configPublish("java")
        }
    }
}

private fun Project.configPublish(componentName: String) {
    apply(plugin = "maven-publish")
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                afterEvaluate {
                    from(components.getByName(componentName))
                }
            }
        }
    }
}