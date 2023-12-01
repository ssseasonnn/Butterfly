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
    namespace = "zlc.season.bar"
    enableCompose()
}

dependencies {
    kapt(project(":compiler"))
    implementation(project(":butterfly"))
    implementation(project(":butterfly-compose"))

    implementation(project(":samples:modules:base"))

    implementation(libs.bundles.android)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.season)
}