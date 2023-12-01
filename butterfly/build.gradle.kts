import zlc.season.buildlogic.base.androidLibrary

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("maven-publish")
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.parcelize)
}

androidLibrary {
    namespace = "zlc.season.butterfly"
}

dependencies {
    api(project(":annotation"))
    api("com.github.ssseasonnn:ClarityPotion:1.0.6")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    api("androidx.fragment:fragment:1.5.4")
    api("androidx.core:core-ktx:1.9.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components.getByName("release"))
            }
        }
    }
}
