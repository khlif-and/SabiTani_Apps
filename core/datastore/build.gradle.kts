plugins {
    id("sabitani.android.library")
    id("sabitani.android.hilt")
}

android {
    namespace = "tech.sabitani.core.datastore"
}

dependencies {
    api(libs.androidx.datastore.preferences)
}
