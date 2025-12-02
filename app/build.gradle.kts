plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.quizzappmb2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.quizzappmb2"
        minSdk = 26 // Hạ xuống 26 (Android 8.0) để cài được trên nhiều máy hơn (35 quá cao)
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BẬT CÁI NÀY ĐỂ CHẠY ĐƯỢC THƯ VIỆN NẶNG
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // QUAN TRỌNG: THÊM ĐOẠN NÀY ĐỂ TRÁNH LỖI KHI BUILD THƯ VIỆN WORD
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/ASL2.0"
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- SUPABASE & MẠNG ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // --- GIAO DIỆN & ẢNH ---
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // --- THƯ VIỆN ĐỌC FILE WORD (.DOCX) - MỚI THÊM ---
    implementation("org.apache.poi:poi-ooxml:5.2.2")
    implementation("com.fasterxml:aalto-xml:1.3.0")
    // --- HỖ TRỢ BUILD ---
    implementation("androidx.multidex:multidex:2.0.1")
}