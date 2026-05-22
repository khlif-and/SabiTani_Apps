plugins {
    id("sabitani.android.library")
    id("sabitani.android.hilt")
}

android {
    namespace = "tech.sabitani.core.network"
}

dependencies {
    api(libs.bundles.network)
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)
}
