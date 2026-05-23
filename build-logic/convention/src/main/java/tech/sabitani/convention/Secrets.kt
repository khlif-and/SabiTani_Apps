package tech.sabitani.convention

import org.gradle.api.Project
import java.util.Properties

fun Project.readSecret(name: String): String {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        val props = Properties().apply { localPropsFile.inputStream().use(::load) }
        props.getProperty(name)?.takeIf(String::isNotBlank)?.let { return it }
    }
    return System.getenv(name) ?: ""
}
