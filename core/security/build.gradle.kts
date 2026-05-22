plugins {
    id("sabitani.android.library")
    id("sabitani.android.hilt")
}

android {
    namespace = "tech.sabitani.core.security"
}

dependencies {
    implementation(libs.androidx.security.crypto)
    api(libs.androidx.biometric)
    implementation(libs.kotlinx.coroutines.core)
}
