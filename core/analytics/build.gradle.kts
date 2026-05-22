plugins {
    id("sabitani.android.library")
    id("sabitani.android.hilt")
}

android {
    namespace = "tech.sabitani.core.analytics"
}

dependencies {
    api(libs.timber)
}
