pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        kotlin("plugin.serialization").version(extra["serialization.version"] as String)
        id("org.jetbrains.compose").version(extra["compose_plugin.version"] as String)
    }
}

rootProject.name = "school_db"

