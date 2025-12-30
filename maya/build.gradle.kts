plugins {
    java
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.gradleup.shadow")
    application
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(project(":website:frontend"))
    implementation(project(":common"))

    // Discord
    implementation(libs.jda)
    implementation("com.github.freya022:jda-ktx:8929de93af")

    // Coroutines and DateTime
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.debug)
    implementation(libs.kotlinx.datetime)

    // Database
    implementation(libs.hikari)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.postgres)

    // Ktor
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.htmx)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)

    // Serialization
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.hocon)
    implementation(libs.jackson.dataformat.yaml)
    implementation(libs.jackson.module.kotlin)

    // Logging
    implementation(libs.logback.classic)
    implementation(libs.kotlin.logging)

    // Caching
    implementation(libs.caffeine)

    // Thread
    implementation(libs.guava)
    implementation("io.ktor:ktor-server-content-negotiation:3.2.2")
    implementation("io.ktor:ktor-server-core:3.2.2")
    implementation("io.ktor:ktor-server-core:3.2.2")
    implementation("io.ktor:ktor-serialization-jackson:3.2.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.2.2")
    implementation("io.ktor:ktor-server-core:3.2.2")
    implementation("io.ktor:ktor-server-sessions:3.2.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.2")
}

@Suppress("DEPRECATION")
val sass = tasks.register<SassTask>("sass-style-scss") {
    this.inputSass.set(file("src/main/styles/style.scss"))
    this.inputSassFolder.set(file("src/main/styles/"))
    this.outputSass.set(file("$buildDir/styles/style-scss"))
}

@Suppress("DEPRECATION")
val globalSass = tasks.register<SassTask>("sass-global-style-scss") {
    this.inputSass.set(file("src/main/styles/global.scss"))
    this.inputSassFolder.set(file("src/main/styles/"))
    this.outputSass.set(file("$buildDir/styles/style-scss"))
}

val skipKotlinJsBuild = (findProperty("net.puffinmay.maya.skipKotlinJsBuild") as String?)?.toBoolean() == true
val skipScssBuild = (findProperty("net.puffinmay.maya.skipScssBuild") as String?)?.toBoolean() == true

tasks.test {
    useJUnitPlatform()
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from("../resources/")

        if (!skipScssBuild) {
            dependsOn(sass)

            from(sass) {
                into("static/v1/assets/css")
            }

            from(globalSass) {
                into("static/v1/assets/css")
            }
        }
    }
}

application {
    mainClass.set("net.puffinmay.maya.MayaLauncher")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(Versions.JVM_TARGET))
    }
}
