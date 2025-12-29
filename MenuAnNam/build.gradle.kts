plugins {

    alias(libs.plugins.android.application) apply false

    alias(libs.plugins.kotlin.android) apply false

    alias(libs.plugins.kotlin.compose) apply false

    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10" apply false

    kotlin("jvm") version "2.2.21"

    id("com.google.devtools.ksp") version "2.2.21-2.0.4" apply false

}