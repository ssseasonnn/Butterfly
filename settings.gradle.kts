@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

pluginManagement {
    includeBuild("buildLogic")

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

rootProject.name = "ButterflyDemo"
include(":annotation")
include(":butterfly")
include(":butterfly-compose")
include(":compiler")
include(":plugin")


include(":samples")
include(":samples:app")
include(":samples:modules")

include(":samples:modules:base")
include(":samples:modules:bar")
include(":samples:modules:foo")

include(":samples:modules:compose")
include(":samples:modules:compose:compose_home")
include(":samples:modules:compose:compose_dashboard")
include(":samples:modules:compose:compose_notifications")

include(":samples:modules:normal")
include(":samples:modules:normal:home")
include(":samples:modules:normal:dashboard")
include(":samples:modules:normal:notifications")
