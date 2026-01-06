pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {

            version("kotlin", "2.2.21")
            version("ktor", "3.2.2")
            version("coroutines", "1.10.2")
            version("serialization", "1.8.1")
            version("logback", "1.5.8")
            version("jackson", "2.18.0")
            version("exposed", "0.61.0")

            library("kotlin-stdlib-jdk8", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8").versionRef("kotlin")

            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("coroutines")
            library("kotlinx-coroutines-debug", "org.jetbrains.kotlinx", "kotlinx-coroutines-debug").versionRef("coroutines")

            library("kotlinx-datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").version("0.3.3")

            library("kotlinx-serialization-core", "org.jetbrains.kotlinx", "kotlinx-serialization-core").versionRef("serialization")
            library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef("serialization")
            library("kotlinx-serialization-hocon", "org.jetbrains.kotlinx", "kotlinx-serialization-hocon").versionRef("serialization")

            library("jda", "net.dv8tion", "JDA").version("6.0.0-preview_DEV")

            library("hikari", "com.zaxxer", "HikariCP").version("5.1.0")
            library("postgres", "org.postgresql", "postgresql").version("42.7.7")

            library("exposed-core", "org.jetbrains.exposed", "exposed-core").versionRef("exposed")
            library("exposed-dao", "org.jetbrains.exposed", "exposed-dao").versionRef("exposed")
            library("exposed-jdbc", "org.jetbrains.exposed", "exposed-jdbc").versionRef("exposed")

            library("ktor-server-core", "io.ktor", "ktor-server-core").versionRef("ktor")
            library("ktor-server-netty", "io.ktor", "ktor-server-netty").versionRef("ktor")
            library("ktor-server-cio", "io.ktor", "ktor-server-cio").versionRef("ktor")
            library("ktor-server-auth", "io.ktor", "ktor-server-auth").versionRef("ktor")
            library("ktor-server-sessions", "io.ktor", "ktor-server-sessions").versionRef("ktor")
            library("ktor-server-content-negotiation", "io.ktor", "ktor-server-content-negotiation").versionRef("ktor")
            library("ktor-server-htmx", "io.ktor", "ktor-server-htmx").versionRef("ktor")

            library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef("ktor")
            library("ktor-client-cio", "io.ktor", "ktor-client-cio").versionRef("ktor")
            library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef("ktor")

            library("ktor-serialization-json", "io.ktor", "ktor-serialization-kotlinx-json").versionRef("ktor")
            library("ktor-serialization-jackson", "io.ktor", "ktor-serialization-jackson").versionRef("ktor")
            library("ktor-serialization-gson", "io.ktor", "ktor-serialization-gson").versionRef("ktor")

            library("ktor-htmx", "io.ktor", "ktor-htmx").versionRef("ktor")
            library("ktor-htmx-html", "io.ktor", "ktor-htmx-html").versionRef("ktor")

            library("jackson-dataformat-yaml", "com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml").versionRef("jackson")
            library("jackson-module-kotlin", "com.fasterxml.jackson.module", "jackson-module-kotlin").versionRef("jackson")

            library("logback-classic", "ch.qos.logback", "logback-classic").versionRef("logback")
            library("kotlin-logging", "io.github.microutils", "kotlin-logging").version("2.1.23")

            library("caffeine", "com.github.ben-manes.caffeine", "caffeine").version("3.1.8")
            library("guava", "com.google.guava", "guava").version("32.1.3-jre")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "maya-parent"

include("common")
include("maya-bot-discord")
include("maya-website:frontend")