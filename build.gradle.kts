plugins {
    kotlin("jvm") version "1.6.10"
}

group = "org.jamplate"
version = "0.4.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains:annotations:23.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}
