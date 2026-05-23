import tech.sabitani.convention.readSecret

plugins {
    id("sabitani.android.library")
    id("sabitani.android.hilt")
}

android {
    namespace = "tech.sabitani.core.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "GEMINI_API_KEY", "\"${readSecret("GEMINI_API_KEY")}\"")
        buildConfigField("String", "GEMINI_MODEL", "\"${readSecret("GEMINI_MODEL")}\"")
    }
}

dependencies {
    api(libs.bundles.network)
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)
}
