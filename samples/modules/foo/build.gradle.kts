import zlc.season.buildlogic.base.androidLibrary

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.butterfly)
}

androidLibrary {
    namespace = "zlc.season.foo"

    viewBinding { enable = true }
}

dependencies {
    kapt(project(":compiler"))
    implementation(project(":butterfly"))

    implementation(project(":samples:modules:base"))
    implementation(libs.bundles.android)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.season)
}