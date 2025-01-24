pluginManagement {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = providers.gradleProperty("plugin.name").get()