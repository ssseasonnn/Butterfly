import zlc.season.buildlogic.base.androidLibrary
import zlc.season.buildlogic.base.enableCompose

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.parcelize)
}

androidLibrary {
    namespace = "zlc.season.butterfly.compose"

    enableCompose()
}

dependencies {
    api(project(":butterfly"))
    api(libs.compose.ui)
    api(libs.compose.runtime)
    api(libs.compose.viewmodel)

    implementation(libs.bundles.unit.test)
}