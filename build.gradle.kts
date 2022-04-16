plugins {
    kotlin("jvm") apply false
}

group = "forte.love"
version = "0.0.1"
description = "在Ktor中使用nacos吧!"

loadLocalProperties()

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}