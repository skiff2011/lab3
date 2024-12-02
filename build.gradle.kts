plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.github.skiff2011.lab3"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.telegram:telegrambots:6.5.0")
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("org.fluentd:fluent-logger:0.3.4")
    implementation("io.micrometer:micrometer-registry-prometheus:1.12.2") // Replace with the latest version
    implementation("io.ktor:ktor-server-netty:2.3.3") // For running an HTTP server to expose metrics
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1") // Replace with the latest version
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}

application {
    // Define the main class for the application
    mainClass.set("com.github.skiff2011.lab3")
}