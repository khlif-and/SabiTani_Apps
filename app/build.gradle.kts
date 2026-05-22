plugins {
    id("sabitani.android.application")
    id("sabitani.android.application.compose")
    id("sabitani.android.hilt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dependency.guard)
}

android {
    namespace = "tech.sabitani.app"

    defaultConfig {
        applicationId = "tech.sabitani.app"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.datastore)
    implementation(projects.core.navigation)
    implementation(projects.core.analytics)
    implementation(projects.core.notifications)

    implementation(libs.timber)

    implementation(projects.feature.splash)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.auth)
    implementation(projects.feature.home)
    implementation(projects.feature.plot)
    implementation(projects.feature.cycle)
    implementation(projects.core.model)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)

    testImplementation(libs.junit)
    testImplementation(libs.konsist)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}

dependencyGuard {
    configuration("releaseRuntimeClasspath")
}
