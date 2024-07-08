val jvmTargetVersion = JavaVersion.VERSION_11
val ktorVersion = "2.3.12"
plugins {
    application
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
}

group = "org.novbicreate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    implementation("eu.vendeli:telegram-bot:6.2.0")
    ksp("eu.vendeli:ksp:6.2.0")
}

tasks {
    compileJava {
        targetCompatibility = jvmTargetVersion.majorVersion
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = jvmTargetVersion.majorVersion
            javaParameters = true
        }
    }
}