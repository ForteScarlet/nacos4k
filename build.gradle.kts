plugins {
    kotlin("jvm") apply false
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}


group = "love.forte.nacos4k"
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
    group = rootProject.group
    version = rootProject.version
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    doPublish()


}

// nexus staging
val sonatypeUsername: String? =
    extra.getIfHas("sonatype.username")?.toString() ?: System.getProperty("sonatype.username")
    ?: System.getenv("SONATYPE_USERNAME")

val sonatypePassword: String? =
    extra.getIfHas("sonatype.password")?.toString() ?: System.getProperty("sonatype.password")
    ?: System.getenv("SONATYPE_PASSWORD")

if (sonatypeUsername != null && sonatypePassword != null) {
    println("sonatypeUsername: $sonatypeUsername")

    nexusPublishing {
        packageGroup.set(rootProject.group.toString())

        useStaging.set(
            project.provider { !project.version.toString().endsWith("SNAPSHOT", ignoreCase = true) }
        )

        transitionCheckOptions {
            maxRetries.set(20)
            delayBetween.set(java.time.Duration.ofSeconds(5))
        }
        repositories {
            sonatype {
                snapshotRepositoryUrl.set(uri(Sonatype.`snapshot-oss`.URL))
                username.set(sonatypeUsername)
                password.set(sonatypePassword)
            }
        }
    }
} else {
    println("[WARN] cannot found sonatype.username or sonatype.password.")
}