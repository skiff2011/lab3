plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.github.skiff2011.lab3"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.telegram:telegrambots:6.5.0")
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("org.fluentd:fluent-logger:0.3.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}