plugins {
    `kotlin-dsl`
}

group = "net.puffinmay"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}