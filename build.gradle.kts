// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
//    id("com.android.application") version "8.2.2" apply false // Or latest stable AGP
//    id("org.jetbrains.kotlin.android") version "1.9.22" apply false // Or latest stable Kotlin
    // id("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false // Add if you use Safe Args
}