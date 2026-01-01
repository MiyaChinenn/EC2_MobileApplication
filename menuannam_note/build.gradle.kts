// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    kotlin("jvm") version "2.2.21"
    id("com.google.devtools.ksp") version "2.2.21-2.0.4" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" // Or your current Kotlin version
}

kotlin {

    jvmToolchain(17) // Use your desired version

}