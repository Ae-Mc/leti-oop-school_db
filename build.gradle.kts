import org.jetbrains.compose.desktop.application.dsl.TargetFormat

group = "ru.ae_mc"
version = "1.0-SNAPSHOT"
val exposedVersion: String by project
val h2Version: String by project
val napierVersion: String by project
val loggingVersion: String by project

plugins {
    kotlin("multiplatform") version "1.5.30"
    id("org.jetbrains.compose") version "1.5.11"
    kotlin("plugin.serialization")
    id("io.kotest.multiplatform") version "5.0.2"
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.lighthousegames:logging:$loggingVersion")
                implementation("androidx.compose.material3:material3:1.1.2")
                implementation("com.darkrockstudios:mpfilepicker:2.1.0")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.+")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.16.+")
                implementation("com.itextpdf:itext7-core:8.0.2")
                implementation("io.github.aakira:napier:$napierVersion")
                implementation("io.github.pdvrieze.xmlutil:core:0.86.2")
                implementation("io.github.pdvrieze.xmlutil:serialization:0.86.2")
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.mariadb.jdbc:mariadb-java-client:3.2.0")
                implementation("org.slf4j:slf4j-api:2.0.9")
                implementation("org.slf4j:slf4j-simple:2.0.9")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                // implementation("com.h2database:h2:$h2Version")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:5.8.0")
                implementation("io.kotest:kotest-assertions-core:5.8.0")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "school_db"
            packageVersion = "1.0.0"
        }
    }
}
