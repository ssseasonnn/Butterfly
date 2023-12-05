import zlc.season.buildlogic.base.androidLibrary

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.butterfly)

    alias(libs.plugins.ksp)
}

androidLibrary {
    namespace = "zlc.season.foo"

    viewBinding { enable = true }

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

    implementation(project(":samples:modules:base"))
    implementation(libs.bundles.android)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.season)
}