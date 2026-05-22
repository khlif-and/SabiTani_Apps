plugins {
    id("sabitani.android.library")
    id("sabitani.android.hilt")
}

android {
    namespace = "tech.sabitani.core.database"
}

dependencies {
    api(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    androidTestImplementation(libs.androidx.room.testing)
}
