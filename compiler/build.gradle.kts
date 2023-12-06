@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
    kotlin("jvm")
}

group = "com.github.ssseasonnn"

dependencies {
    implementation(project(":annotation"))
    implementation(libs.kotlin.poet)
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.20-1.0.14")
}