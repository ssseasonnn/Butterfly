import zlc.season.buildlogic.base.androidLibrary
import zlc.season.buildlogic.base.enableCompose

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.butterfly)
}

androidLibrary {
    namespace = "zlc.season.base"
}

dependencies {
    kapt(project(":compiler"))
    implementation(project(":butterfly"))
    implementation(libs.bundles.android)
    implementation(libs.bundles.kotlin)
}