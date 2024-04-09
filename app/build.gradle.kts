plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.btl_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.btl_android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.media3:media3-common:1.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //  Scalable Size Unit
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")

    //  Rounded ImageView
    implementation("com.makeramen:roundedimageview:2.3.0")

    //  Multidex
    implementation("androidx.multidex:multidex:2.0.1")

    //  JSON
    implementation("com.google.code.gson:gson:2.10.1")

    //  ReminderNote?
    implementation("androidx.work:work-runtime:2.9.0")

    //  Thư viện cung cấp API lấy sinh trắc học
    implementation("androidx.biometric:biometric:1.1.0")
}