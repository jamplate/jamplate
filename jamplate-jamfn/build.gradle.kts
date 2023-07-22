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
            implementation(kotlin("stdlib"))
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
