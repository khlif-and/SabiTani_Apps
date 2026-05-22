plugins {
    id("sabitani.android.library")
    id("sabitani.android.hilt")
}

android {
    namespace = "tech.sabitani.core.database"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    api(projects.core.model)
    api(libs.bundles.room)
    implementation(libs.kotlinx.datetime)
    ksp(libs.androidx.room.compiler)

    androidTestImplementation(libs.androidx.room.testing)
}
