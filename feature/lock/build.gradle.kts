plugins {
    id("sabitani.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "tech.sabitani.feature.lock"
}

dependencies {
    implementation(projects.core.security)
    implementation(projects.core.analytics)
}
