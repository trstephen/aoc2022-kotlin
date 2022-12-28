import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    application
}

group = "org.trstephen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
}

sourceSets.main {
    kotlin.srcDir("src/")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.test {
    useJUnitPlatform()
}

// Is there a way to fold this in to the 'kotlin' block???
tasks.withType<KotlinCompile>().all {
    kotlinOptions.allWarningsAsErrors = true
}
