plugins {
    id("sabitani.android.library")
    id("sabitani.android.hilt")
}

android {
    namespace = "tech.sabitani.core.analytics"
}

dependencies {
    api(libs.timber)

    implementation(projects.core.datastore)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}
