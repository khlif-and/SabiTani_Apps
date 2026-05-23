plugins {
    id("sabitani.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "tech.sabitani.feature.tania"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(libs.kotlinx.datetime)
}
