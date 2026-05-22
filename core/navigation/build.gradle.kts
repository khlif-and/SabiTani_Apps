plugins {
    id("sabitani.android.library")
    id("sabitani.android.library.compose")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "tech.sabitani.core.navigation"
}

dependencies {
    api(libs.androidx.navigation.compose)
    api(libs.kotlinx.serialization.json)
}
