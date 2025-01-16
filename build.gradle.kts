import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

plugins {
//    id("org.jetbrains.intellij.platform.settings") version "2.2.0"
//    id("org.jetbrains.intellij.platform.module") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.2.1"
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("java")
}
group = "blog.dekun.wang"
version = "1.0-SNAPSHOT"

repositories {
    intellijPlatform {
        defaultRepositories()
    }
    mavenCentral()
}
//tasks.withType(Jar) {
//    duplicatesStrategy = DuplicatesStrategy.INCLUDE
//}
tasks.withType(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
tasks {
    runIde {
        jvmArgumentProviders += CommandLineArgumentProvider {
            listOf(
                "--add-opens=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED",
                "--add-opens=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED",
                "-javaagent:/Users/wdk/Software-config/ja-netfilter-all/jetbra/ja-netfilter.jar=jetbrains"
            )
        }
    }
}
//https://plugins.jetbrains.com/docs/intellij/custom-plugin-repository.html#describing-plugins-in-updatepluginsxml-file
// 自定义插件仓库文档
dependencies {
    intellijPlatform {
//        intellijIdeaCommunity("2024.3.1")
        intellijIdeaUltimate("2024.3.1")
        bundledPlugin("com.jetbrains.plugins.webDeployment")
//        bundledPlugin("org.jetbrains.idea.maven")
//        bundledPlugin("org.jetbrains.idea.maven.ext")
//        bundledPlugin("org.jetbrains.idea.maven.model")
//        bundledPlugin("org.jetbrains.idea.maven.server.api")
//        bundledPlugin("com.intellij")
//        bundledPlugin("com.intellij.java")
//        bundledPlugin("Git4Idea")
//        bundledPlugin("org.jetbrains.plugins.gradle")
//        bundledPlugin("org.jetbrains.idea.maven")

    }
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.25")
}

