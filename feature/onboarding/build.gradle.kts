plugins {
    id("sabitani.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "tech.sabitani.feature.onboarding"
}

dependencies {
    implementation(projects.core.datastore)
}
