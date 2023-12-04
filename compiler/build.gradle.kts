@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
    kotlin("jvm")
    kotlin("kapt")
}

group = "com.github.ssseasonnn"

dependencies {
    implementation(project(":annotation"))
    implementation(libs.kotlin.poet)

    implementation(libs.auto.service)
    kapt(libs.auto.service)
}