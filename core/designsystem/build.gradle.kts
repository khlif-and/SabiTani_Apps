plugins {
    id("sabitani.android.library")
    id("sabitani.android.library.compose")
}

android {
    namespace = "tech.sabitani.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.icons.extended)

    debugApi(libs.androidx.compose.ui.tooling)
}
