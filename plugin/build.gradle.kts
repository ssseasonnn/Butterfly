plugins {
    id("kotlin")
    id("groovy")
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version ("0.18.0")
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation(libs.android.gradle)
    implementation(libs.android.gradle.api)
}

group = "io.github.ssseasonnn"
version = "1.0.1"

gradlePlugin {
    plugins {
        create("butterflyPlugin") {
            id = "io.github.ssseasonnn.butterfly"
            displayName = "Butterfly plugin"
            description = "Butterfly plugin"
            implementationClass = "zlc.season.butterfly.plugin.ButterflyPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/ssseasonnn/Butterfly"
    vcsUrl = "https://github.com/ssseasonnn/Butterfly.git"
    tags = listOf("Butterfly", "Plugin")
}

tasks.withType<Copy>().all {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.ssseasonnn"
            artifactId = "io.github.ssseasonnn.butterfly"
            version = "1.0.1"

            from(components.getByName("java"))
        }
    }
    repositories {
        mavenLocal()
    }
}