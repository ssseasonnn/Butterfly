plugins {
    id("kotlin")
    id("maven-publish")
}

group = "com.github.ssseasonnn"

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("java"))
        }
    }
}