plugins {
    id("sabitani.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "tech.sabitani.feature.home"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(libs.kotlinx.datetime)
}
