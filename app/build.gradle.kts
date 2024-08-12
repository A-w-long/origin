import java.util.Date
import java.text.SimpleDateFormat
plugins {
    alias(libs.plugins.android.application)
}

val timestamp = SimpleDateFormat("MM_dd_HH_mm").format(Date())
android {
    namespace = "com.sketch.papertracingart"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sketch.papertracingart"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        setProperty("archivesBaseName", "AR Draw Sketch_V" + versionName + "(${versionCode})_$timestamp")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.material.v140)
    implementation(libs.viewpager2)
    implementation(libs.recyclerview)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation (libs.camera.core)
    implementation (libs.camera.camera2)
    implementation (libs.camera.lifecycle)
    implementation (libs.camera.view.v100alpha31)
    implementation (libs.camera.extensions)
    implementation (libs.room.runtime)
    annotationProcessor (libs.room.compiler)
    implementation (libs.recyclerview.v121)
}