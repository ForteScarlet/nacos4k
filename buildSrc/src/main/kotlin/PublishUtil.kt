/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.SigningExtension


fun Project.doPublish(artifactId: String = name) {
    afterEvaluate {
        configurePublishing(artifactId)
        println("[publishing-configure] - [$artifactId] configured.")
        // set gpg file path to root
        val secretKeyRingFileKey = "signing.secretKeyRingFile"
        // val secretKeyRingFile = local().getProperty(secretKeyRingFileKey) ?: throw kotlin.NullPointerException(secretKeyRingFileKey)
        val secretRingFile = rootProject.file("ForteScarlet.gpg")
        println("secretRingFile exists: ${secretRingFile.exists()}")
        extra[secretKeyRingFileKey] = secretRingFile
        setProperty(secretKeyRingFileKey, secretRingFile)

        extensions.configure<SigningExtension>("signing") {
            sign(extensions.getByName<PublishingExtension>("publishing").publications)
        }
    }
}


fun Project.configurePublishing(artifactId: String) {
    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    // val sourcesJar = tasks["sourcesJar"]
    val javadocJar = tasks.register("javadocJar", Jar::class) {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        archiveClassifier.set("javadoc")
    }

    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])

                groupId = project.group.toString()
                setArtifactId(artifactId)
                version = project.version.toString()

                setupPom(project = project)
                showPom()

                artifact(sourcesJar)
                artifact(javadocJar.get())
            }
        }

        repositories {
            mavenLocal()

            val username0 = rootProject.extra.getIfHas("sonatype.username")?.toString() ?: run {
                println("[WARN] Cannot found sonatype.username from extra for $artifactId")
                return@repositories
            }

            val password0 = rootProject.extra.get("sonatype.password")?.toString()
                ?: throw NullPointerException("sonatype-password")

            maven {
                if (version.toString().contains("SNAPSHOT", true)) {
                    // snapshot
                    name = Sonatype.`snapshot-oss`.NAME
                    url = uri(Sonatype.`snapshot-oss`.URL)
                } else {
                    name = Sonatype.oss.NAME
                    url = uri(Sonatype.oss.URL)
                }

                println("Publish repository name: $name")
                println("Publish repository url:  $url")


                credentials {
                    username = username0
                    password = password0
                }
            }
        }

    }

}

fun Project.configurePublishingLocal(artifactId: String) {

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    // val sourcesJar = tasks["sourcesJar"]
    val javadocJar = tasks.register("javadocJar", Jar::class) {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        archiveClassifier.set("javadoc")
    }

    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])

                groupId = project.group.toString()
                setArtifactId(artifactId)
                version = project.version.toString()

                setupPom(project = project)
                showPom()

                artifact(sourcesJar)
                artifact(javadocJar.get())
            }
        }

        repositories {
            mavenLocal()
        }

    }

}

fun MavenPublication.showPom() {
    println("======= pom $artifactId =======")
    println("groupId:    $groupId")
    println("artifactId: $artifactId")
    println("version:    $version")
    println("complete:   $groupId:$artifactId:$version")
    println()
}


fun MavenPublication.setupPom(project: Project) {
    val githubHome = "https://github.com/ForteScarlet/nacos4k"
    pom {
        scm {
            url.set(githubHome)
            connection.set("scm:$githubHome.git")
            developerConnection.set("scm:${githubHome.replace("https:", "git:")}.git")
        }

        issueManagement {
            system.set("GitHub Issue Management")
            url.set("$githubHome/issues")
        }

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("forte")
                name.set("ForteScarlet")
                email.set("ForteScarlet@163.com")
            }
        }

        withXml {
            val root = asNode()
            root.appendNode("description", project.description)
            root.appendNode("name", project.name)
            root.appendNode("url", githubHome)
        }


    }

}


inline val Project.sourceSets: SourceSetContainer
    get() = extensions.getByName("sourceSets") as SourceSetContainer


fun Project.publishing(configure: PublishingExtension.() -> Unit) {
    (this as ExtensionAware).extensions.configure("publishing", configure)
}


@Suppress("ClassName")
object Sonatype {
    object oss {
        const val NAME = "oss"
        const val URL = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
    }

    object `snapshot-oss` {
        const val NAME = "snapshot-oss"
        const val URL = "https://oss.sonatype.org/content/repositories/snapshots/"

    }
}


fun ExtraPropertiesExtension.getIfHas(key: String): Any? {
    return if (has(key)) get(key) else null
}