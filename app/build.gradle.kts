import com.android.build.gradle.internal.packaging.defaultExcludes

plugins {
    id("com.android.application")



}


android {
    namespace = "com.antozstudios.myapplication"
    compileSdk = 35


packaging{


    resources.excludes.add("META-INF/DEPENDENCIES")
    resources.excludes.add("META-INF/INDEX.LIST");
    resources.excludes.add("mozilla/public-suffix-list.txt");
}


    defaultConfig {
        applicationId = "com.antozstudios.kidify"
        minSdk = 34
        targetSdk = 35
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
    compileOptions {

        sourceCompatibility = JavaVersion.current()
        targetCompatibility =  JavaVersion.current()
    }
}

dependencies {
    // deine anderen Abhängigkeiten hier...



    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.compose.foundation:foundation-android:1.6.3")
    implementation("com.google.ar.sceneform:filament-android:1.17.1")

    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.cloud:google-cloud-texttospeech:2.37.0")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.3")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.12.3");
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.2")
    implementation ("androidx.multidex:multidex:2.0.1")







}



