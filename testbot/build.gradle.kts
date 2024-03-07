plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

group = "com.pattexpattex"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation(rootProject)
    runtimeOnly("ch.qos.logback:logback-classic:1.4.14")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "dord.testbot.MainKt"
}