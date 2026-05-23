pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SabiTani"
include(":app")
include(":core:common")
include(":core:model")
include(":core:designsystem")
include(":core:ui")
include(":core:network")
include(":core:datastore")
include(":core:database")
include(":core:data")
include(":core:security")
include(":core:navigation")
include(":core:analytics")
include(":core:notifications")
include(":feature:splash")
include(":feature:onboarding")
include(":feature:auth")
include(":feature:plot")
include(":feature:cycle")
include(":feature:home")
include(":feature:lock")
include(":feature:tania")
