import org.jdom2.CDATA
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

plugins {
//    id("org.jetbrains.intellij.platform.settings") version "2.2.0"
//    id("org.jetbrains.intellij.platform.module") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.2.1"
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("java")
}
group = "blog.dekun.wang"

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
    val homeDir = System.getenv("HOME")
    runIde {
        jvmArgumentProviders += CommandLineArgumentProvider {
            listOf(
                "--add-opens=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED",
                "--add-opens=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED",
                "-javaagent:${homeDir}/Software-config/ja-netfilter-all/jetbra/ja-netfilter.jar=jetbrains"
            )
        }
    }
}
intellijPlatform {
    pluginConfiguration {
        id = providers.gradleProperty("plugin.id")
        name = providers.gradleProperty("plugin.name")
        version = providers.gradleProperty("plugin.version")
        description = providers.gradleProperty("plugin.description")
        changeNotes = providers.gradleProperty("plugin.changeNotes")
        vendor {
            name = providers.gradleProperty("plugin.vendor.name")
            email = providers.gradleProperty("plugin.vendor.email")
            url = providers.gradleProperty("plugin.vendor.url")
        }
        ideaVersion {
            sinceBuild = providers.gradleProperty("plugin.ideaVersion.sinceBuild")
            untilBuild = provider { null }
        }
    }
}
//https://plugins.jetbrains.com/docs/intellij/custom-plugin-repository.html#describing-plugins-in-updatepluginsxml-file
// 自定义插件仓库文档
dependencies {
    intellijPlatform {
        version = providers.gradleProperty("plugin.version").get()
        intellijIdeaUltimate("2024.3.2")
        bundledPlugin("hg4idea")
        bundledPlugin("Git4Idea")
//        intellijIdeaCommunity("2024.3.1")
//        bundledPlugin("com.jetbrains.plugins.webDeployment")
//        bundledPlugin("Git4Idea")
//        bundledPlugin("org.jetbrains.idea.maven")
//        bundledPlugin("org.jetbrains.idea.maven.ext")
//        bundledPlugin("org.jetbrains.idea.maven.model")
//        bundledPlugin("org.jetbrains.idea.maven.server.api")
//        bundledPlugin("com.intellij")
//        bundledPlugin("com.intellij.java")

//        bundledPlugin("org.jetbrains.plugins.gradle")
//        bundledPlugin("org.jetbrains.idea.maven")

    }
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.25")
}


buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jdom:jdom2:2.0.6.1")
    }
}

//updatePluginXml -Purl=test
tasks.register("updatePluginXml") {
    val basePath = project.projectDir
    val nowVersion = providers.gradleProperty("plugin.version").get()
    val pluginId = providers.gradleProperty("plugin.id").get()
    val pluginName = providers.gradleProperty("plugin.name").get()
    val pluginDescription = providers.gradleProperty("plugin.description").get()
    val pluginChangeNotes = providers.gradleProperty("plugin.changeNotes").get()
    val vendorName = providers.gradleProperty("plugin.vendor.name").get()
    val vendorEmail = providers.gradleProperty("plugin.vendor.email").get()
    val vendorUrl = providers.gradleProperty("plugin.vendor.url").get()
    val ideaSinceBuild = providers.gradleProperty("plugin.ideaVersion.sinceBuild").get()
    val downloadUrl = providers.gradleProperty("url").getOrElse("no-download-url")
    doLast {
        val xmlFile = File("${basePath}/updatePlugins.xml")
        val document: Document = SAXBuilder().build(xmlFile)
        val rootElement: Element = document.rootElement
        var pluginExists = false
        for (pluginNode in rootElement.getChildren("plugin")) {
            if (pluginNode.getChildText("version") == nowVersion || pluginNode.getChildText("version").contains("SNAPSHOT")) {
                pluginNode.apply {
                    getChild("name").text = pluginName
                    getChild("vendor").apply {
                        content.clear()
                        text = vendorName
                        setAttribute("email", vendorEmail)
                        setAttribute("url", vendorUrl)
                    }
                    getChild("description").apply {
                        content.clear()
                        addContent(CDATA(pluginDescription))
                    }
                    getChild("change-notes").apply {
                        content.clear()
                        addContent(CDATA(pluginChangeNotes))
                    }
                    getChild("idea-version").setAttribute("since-build", ideaSinceBuild)
                    getChild("download-url").text = downloadUrl
                }
                pluginExists = true
                break
            }
        }
        if (!pluginExists) {
            rootElement.addContent(0, Element("plugin").apply {
                addContent(Element("id").apply {
                    text = pluginId
                })
                addContent(Element("name").apply {
                    text = pluginName
                })
                addContent(Element("version").apply {
                    text = nowVersion
                })
                addContent(Element("idea-version").apply {
                    setAttribute("since-build", ideaSinceBuild)
                })
                addContent(Element("vendor").apply {
                    text = vendorName
                    setAttribute("email", vendorEmail)
                    setAttribute("url", vendorUrl)
                })
                addContent(Element("description").apply {
                    addContent(CDATA(pluginDescription))
                })
                addContent(Element("change-notes").apply {
                    addContent(CDATA(pluginChangeNotes))
                })
                addContent(Element("download-url").apply {
                    text = downloadUrl
                })
            })
        }
        XMLOutputter().apply {
            format = Format.getPrettyFormat().apply {
                indent += indent
                encoding = "UTF-8"
            }
            output(document, xmlFile.writer())
        }
    }
}