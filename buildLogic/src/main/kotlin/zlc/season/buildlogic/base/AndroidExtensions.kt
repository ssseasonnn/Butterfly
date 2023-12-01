@file:Suppress("UnstableApiUsage")

package zlc.season.buildlogic.base

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.dsl.BuildType
import org.gradle.api.JavaVersion
import org.gradle.api.Project

fun Project.androidApplication(block: BaseAppModuleExtension.() -> Unit = {}) {
    configBase().applyAs<BaseAppModuleExtension> {
        block()
    }
    configPackaging()
}

fun Project.androidLibrary(block: LibraryExtension.() -> Unit = {}) {
    configBase().applyAs<LibraryExtension> {
        block()
    }
    configPackaging()
}

private fun Project.configBase(): BaseExtension {
    return android.apply {
        val libs = getLibs()
        setCompileSdkVersion(libs.getVersion("sdk-compile-version"))

        defaultConfig {
            minSdk = libs.getVersion("sdk-min-version")
            targetSdk = libs.getVersion("sdk-target-version")
            vectorDrawables { useSupportLibrary = true }

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }

        testOptions {
            unitTests {
                isReturnDefaultValues = true
                isIncludeAndroidResources = true
            }
        }
    }
}

private fun Project.configPackaging() {
    android.applyAs<CommonExtension<*, *, *, *>> {
        packaging {
            resources.excludes.add("META-INF/LICENSE.md")
            resources.excludes.add("META-INF/LICENSE-notice.md")
        }
    }
}

fun Project.enableCompose() {
    val libs = getLibs()

    android.apply {
        buildFeatures.compose = true

        composeOptions {
            kotlinCompilerExtensionVersion = libs.getVersionStr("compose-compiler")
        }
    }
}

fun Project.addDefaultConstant(vararg constantPair: Pair<String, String>) {
    android.apply {
        defaultConfig {
            val addDefaultConstantLambda = createAddDefaultConstantLambda()
            constantPair.forEach {
                addDefaultConstantLambda(it.first, it.second)
            }
        }
    }
}