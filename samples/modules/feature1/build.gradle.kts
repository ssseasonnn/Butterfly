import zlc.season.buildlogic.base.androidLibrary

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.butterfly)
}

androidLibrary {
    namespace = "zlc.season.feature1"

    viewBinding { enable = true }
}

dependencies {
    ksp(project(":compiler"))
    implementation(project(":butterfly"))

    implementation(project(":samples:modules:base"))
    implementation(libs.bundles.android)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.season)
}