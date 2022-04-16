val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    `java-library`
    kotlin("jvm")
}

group = "forte.love"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("com.alibaba.nacos:nacos-client:2.0.4")
    implementation("io.ktor:ktor-client-core:$ktor_version")

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        javaParameters = true
    }
}


kotlin {
    explicitApiWarning()
    sourceSets.all {
        languageSettings {
            optIn("kotlin.RequiresOptIn")
        }
    }

}