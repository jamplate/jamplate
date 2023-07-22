plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        withJava()
    }
}

kotlin.sourceSets {
    val commonMain by getting {
        dependencies {
            implementation(project(":jamplate-jamcore"))

            implementation(kotlin("stdlib"))

            api(libs.jetbrains.annotations.jvm) // JVM specific!
        }
    }
    val commonTest by getting {
        dependencies {
            implementation(kotlin("test"))
        }
    }

    val jvmMain by getting
    val jvmTest by getting
}
