plugins {
    id("build.logic")
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.kover) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.hilt) apply false

    id("io.github.ssseasonnn.butterfly") version ("1.0.1") apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}