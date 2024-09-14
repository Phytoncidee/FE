import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt") // Kotlin Annotation Processing Tool
    id("kotlin-android")
    id("kotlin-parcelize")

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {

    namespace = "com.example.hikinglog_fe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hikinglog_fe"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // 카카오 키 가져오기
        buildConfigField("String","KAKAO_API_KEY", getApiKey("KAKAO_API_KEY"))
        manifestPlaceholders["KAKAO_API_KEY"] = getApiKey("KAKAO_API_KEY")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("armeabi-v7a")
            abiFilters.add("x86")
            abiFilters.add("x86_64")
        }

    }

    buildFeatures {
        buildConfig = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // [뷰 바인딩]
    viewBinding{
        enable = true
    }
}

fun getApiKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(propertyKey)
}

dependencies {

    // Kakao
    implementation("com.kakao.sdk:v2-user:2.20.3")
    implementation("com.kakao.sdk:v2-share:2.20.3")
    implementation("com.kakao.maps.open:android:2.11.9")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0'")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.8.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.8.0")


    // 이미지 처리-Glide 라이브러리
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Lottie - 날씨
    implementation("com.airbnb.android:lottie:5.0.2")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.core:core:1.10.1")


    // [spring 서버 통신]

    // retrofit2 사용 등록
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Json - gson
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // String
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // XML - tikxml
    implementation("com.tickaroo.tikxml:annotation:0.8.13")
    implementation("com.tickaroo.tikxml:core:0.8.13")
    implementation("com.tickaroo.tikxml:retrofit-converter:0.8.13")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    kapt ("com.tickaroo.tikxml:processor:0.8.13")

    // 이미지 처리-Glide 라이브러리
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation ("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // 관광 코스 생성_구글 지도 이용
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // 로딩 이미지
    implementation ("com.airbnb.android:lottie:6.0.0")

}