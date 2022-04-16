rootProject.name = "nacos4k"


pluginManagement {
    repositories {
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }

    plugins {
        kotlin("jvm") version "1.6.20" apply false
        id("org.jetbrains.dokka") version "1.6.20"
    }
}

include("nacos4k-api")
include("nacos4k-server")
include("nacos4k-client")