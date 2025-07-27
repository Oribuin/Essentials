plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
}
group = "dev.oribuin"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    disableAutoTargetJvm()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://nexus.neetgames.com/repository/maven-snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    api("dev.rosewood:rosegarden:1.5.4-SNAPSHOT")
    api("net.objecthunter:exp4j:0.4.8")
    api("com.jeff-media:MorePersistentDataTypes:2.4.0")

    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")

    // External Plugins
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("org.black_ixx:playerpoints:3.2.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "*")
    }
}

tasks {

    this.compileJava {
        this.options.compilerArgs.add("-parameters")
        this.options.isFork = true
        this.options.encoding = "UTF-8"
    }

    this.shadowJar {
        this.archiveClassifier.set("")

        this.relocate("dev.rosewood.rosegarden", "${project.group}.essentials.libs.rosegarden")
        this.relocate("net.objecthunter.exp4j", "${project.group}.essentials.libs.exp4j")
        this.relocate("com.jeff_media", "${project.group}.essentials.libs.morepersistentdatatypes")
        this.relocate("org.jetbrains", "${project.group}.essentials.libs.jetbrains")

        // rosegarden should be relocating this
        this.relocate("com.zaxxer", "dev.rosewood.rosegarden.libs.hikari")
        this.relocate("org.slf4j", "dev.rosewood.rosegarden.libs.slf4j")
    }

    this.processResources {
        this.expand("version" to project.version)
    }

    this.build {
        this.dependsOn(shadowJar)
    }
}