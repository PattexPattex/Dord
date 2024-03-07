buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.23.2")
    }
}

apply(plugin = "kotlinx-atomicfu")

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("me.qoomon.git-versioning") version "6.4.2"
}

group = "com.pattexpattex"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    api("net.dv8tion:JDA:5.0.0-beta.20")
    api("com.github.minndevelopment:jda-ktx:78dbf82")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation(kotlin("reflect"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

gitVersioning.apply {
    refs {
        tag("v(?<version>.*)") { version = "\${ref.version}" }
    }
    rev {
        version = "\${commit.short}"
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.group as String
            artifactId = project.name.lowercase()
            version = project.version as String

            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)
}