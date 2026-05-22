plugins {
    id("sabitani.android.library")
    id("sabitani.android.library.compose")
}

android {
    namespace = "tech.sabitani.core.ui"
}

dependencies {
    api(projects.core.designsystem)
}
