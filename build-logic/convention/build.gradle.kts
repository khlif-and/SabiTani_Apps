plugins {
    `kotlin-dsl`
}

group = "tech.sabitani.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "sabitani.android.application"
            implementationClass = "tech.sabitani.convention.AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "sabitani.android.application.compose"
            implementationClass = "tech.sabitani.convention.AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "sabitani.android.library"
            implementationClass = "tech.sabitani.convention.AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "sabitani.android.library.compose"
            implementationClass = "tech.sabitani.convention.AndroidLibraryComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "sabitani.android.hilt"
            implementationClass = "tech.sabitani.convention.AndroidHiltConventionPlugin"
        }
        register("kotlinJvm") {
            id = "sabitani.kotlin.jvm"
            implementationClass = "tech.sabitani.convention.KotlinJvmConventionPlugin"
        }
        register("androidFeature") {
            id = "sabitani.android.feature"
            implementationClass = "tech.sabitani.convention.AndroidFeatureConventionPlugin"
        }
    }
}
