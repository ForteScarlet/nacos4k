val ktor_version: String by project

plugins {
    `java-library`
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

group = "forte.love"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation(project(":nacos4k-api"))
    implementation("io.ktor:ktor-client-core:$ktor_version")


    compileOnly("com.alibaba.nacos:nacos-api:2.0.4") {
        exclude("*", "*")
    }
    testImplementation("io.ktor:ktor-client-cio:$ktor_version")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    testImplementation("io.ktor:ktor-serialization-jackson:$ktor_version")
    testImplementation("love.forte.simbot:simbot-logger:3.0.0.preview.7.0")
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