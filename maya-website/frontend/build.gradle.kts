plugins {
    kotlin("jvm") version Versions.KOTLIN
}

group = "net.puffinmay.maya.maya-website"
version = Versions.MAYA_VERSION

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    implementation(libs.ktor.htmx)
    implementation(libs.ktor.htmx.html)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(Versions.JVM_TARGET))
    }
}