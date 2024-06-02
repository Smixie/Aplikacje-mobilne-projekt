plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.remembermymovies"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.remembermymovies"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // View Model
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")

    // Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Circular Progress Indicator
    implementation("com.github.antonKozyriatskyi:CircularProgressIndicator:1.3.0")
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database)
    implementation(libs.androidx.drawerlayout)

    testImplementation(libs.junit)

    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}