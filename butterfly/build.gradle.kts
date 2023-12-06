import zlc.season.buildlogic.base.androidLibrary

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.parcelize)
}

androidLibrary {
    namespace = "zlc.season.butterfly"
}

dependencies {
    api(project(":annotation"))
    api(libs.clarity)
    api(libs.kotlin.coroutines)
    api(libs.lifecycle.runtime.ktx)
    api(libs.fragment.ktx)
    api(libs.core.ktx)

    implementation(libs.bundles.unit.test)
}