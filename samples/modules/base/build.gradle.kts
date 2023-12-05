import zlc.season.buildlogic.base.androidLibrary
import zlc.season.buildlogic.base.enableCompose

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.butterfly)
    alias(libs.plugins.ksp)
}

androidLibrary {
    namespace = "zlc.season.base"

    buildTypes {
        debug {
            kotlin {
                sourceSets {
                    main {
                        kotlin.srcDir("build/generated/ksp/debug/kotlin")
                    }
                }
            }
        }
    }
}

dependencies {
    ksp(project(":compiler"))
    implementation(project(":butterfly"))
    implementation(libs.bundles.android)
    implementation(libs.bundles.kotlin)
}