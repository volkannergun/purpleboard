plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // If you use libraries like Room or Dagger later
    // id("androidx.navigation.safeargs.kotlin") // Add if you use Safe Args
}

android {
    namespace = "com.example.purpleboard" // IMPORTANT: Match your package name
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.purpleboard" // IMPORTANT: Match your package name
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Set to true for production release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        // dataBinding = true // Uncomment if you plan to use DataBinding
    }
    packagingOptions { // Add this if you get duplicate file errors from libraries
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0") // Update to latest stable
    implementation("androidx.appcompat:appcompat:1.6.1") // Update to latest stable
    implementation("com.google.android.material:material:1.11.0") // Update to latest stable Material3
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle (ViewModel, LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0") // Update to latest stable
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0") // Update to latest stable
    implementation("androidx.activity:activity-ktx:1.8.2") // For by viewModels() delegate in Activity
    implementation("androidx.fragment:fragment-ktx:1.6.2") // For by viewModels() delegate in Fragment

    // Networking (Retrofit, Gson)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // For logging network requests

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Update to latest stable
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Update to latest stable

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7") // Update to latest stable
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7") // Update to latest stable

    // Image Loading (Glide)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // kapt("com.github.bumptech.glide:compiler:4.16.0") // Glide v4 doesn't strictly need kapt for basic use

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}