@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import zlc.season.buildlogic.base.androidApplication
import zlc.season.buildlogic.base.enableCompose

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("io.github.ssseasonnn.butterfly")
    id("kotlin-kapt")
}


androidApplication {
    namespace = "zlc.season.butterflydemo"

    viewBinding { enable = true }
    enableCompose()
}

dependencies {
    implementation(project(":samples:modules:normal:home"))
    implementation(project(":samples:modules:normal:dashboard"))
    implementation(project(":samples:modules:normal:notifications"))

    implementation(project(":samples:modules:compose:compose_home"))
    implementation(project(":samples:modules:compose:compose_dashboard"))
    implementation(project(":samples:modules:compose:compose_notifications"))

    implementation(project(":samples:modules:base"))
    implementation(project(":samples:modules:foo"))
    implementation(project(":samples:modules:bar"))

    kapt(project(":compiler"))
    implementation(project(":butterfly"))
    implementation(project(":butterfly-compose"))
    implementation(libs.bundles.android)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.season)

    debugImplementation(libs.leakcanary)

}