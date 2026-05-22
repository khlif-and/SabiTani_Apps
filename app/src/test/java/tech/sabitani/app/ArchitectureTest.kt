package tech.sabitani.app

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class ArchitectureTest {
    @Test
    fun `domain layer must not depend on Android framework`() {
        Konsist
            .scopeFromProduction()
            .files
            .filter { it.packagee?.name?.contains(".domain") == true }
            .assertTrue { file ->
                file.imports.none { import ->
                    import.name.startsWith("android.") ||
                        import.name.startsWith("androidx.") ||
                        import.name.startsWith("com.android.")
                }
            }
    }

    @Test
    fun `domain layer must not depend on Retrofit or Room`() {
        Konsist
            .scopeFromProduction()
            .files
            .filter { it.packagee?.name?.contains(".domain") == true }
            .assertTrue { file ->
                file.imports.none { import ->
                    import.name.startsWith("retrofit2.") ||
                        import.name.startsWith("androidx.room.") ||
                        import.name.startsWith("okhttp3.")
                }
            }
    }

    @Test
    fun `feature module must not depend on another feature module`() {
        Konsist
            .scopeFromProduction()
            .files
            .filter { it.packagee?.name?.startsWith("tech.sabitani.feature.") == true }
            .assertTrue { file ->
                val ownFeaturePackage =
                    file.packagee
                        ?.name
                        ?.split('.')
                        ?.take(4)
                        ?.joinToString(".")
                        .orEmpty()
                file.imports.none { import ->
                    import.name.startsWith("tech.sabitani.feature.") &&
                        !import.name.startsWith(ownFeaturePackage)
                }
            }
    }
}
