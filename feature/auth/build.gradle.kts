plugins {
    id("sabitani.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "tech.sabitani.feature.auth"
}
