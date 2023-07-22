plugins {
    kotlin("multiplatform") version libs.versions.kotlin apply false
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
}

tasks.wrapper {
    gradleVersion = "8.2.1"
}

subprojects {
    group = "org.jamplate"
    version = "0.4.0"

    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
