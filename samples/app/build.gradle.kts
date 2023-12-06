@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import zlc.season.buildlogic.base.androidApplication
import zlc.season.buildlogic.base.enableCompose

plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    id("io.github.ssseasonnn.butterfly")
}


androidApplication {
    namespace = "zlc.season.butterflydemo"

    viewBinding { enable = true }
    enableCompose()
}


ksp {
    arg("butterfly.log.enable", "true")
}

dependencies {
    implementation(project(":samples:modules:normal:home"))
    implementation(project(":samples:modules:normal:dashboard"))
    implementation(project(":samples:modules:normal:notifications"))

    implementation(project(":samples:modules:compose:compose_home"))
    implementation(project(":samples:modules:compose:compose_dashboard"))
    implementation(project(":samples:modules:compose:compose_notifications"))

    implementation(project(":samples:modules:base"))
    implementation(project(":samples:modules:feature1"))
    implementation(project(":samples:modules:feature2"))

    ksp(project(":compiler"))
    implementation(project(":butterfly"))
    implementation(project(":butterfly-compose"))
    implementation(libs.bundles.android)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.season)

    debugImplementation(libs.leakcanary)

}