plugins {
    kotlin("jvm") apply false
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}


group = "forte.love.nacos4k"
version = "0.0.1"
description = "在Ktor中使用nacos吧!"

ext {
    loadLocalProperties(rootProject)
}


repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    afterEvaluate {

        configurePublishing(name)
        println("[publishing-configure] - [$name] configured.")

        signing {
            val secretRingFile = rootProject.file("ForteScarlet.gpg") // extra["signing.secretKeyRingFilePath"]
            extra["signing.secretKeyRingFile"] = secretRingFile
            setProperty("signing.secretKeyRingFile", secretRingFile)

            sign(publishing.publications)
        }
    }

}