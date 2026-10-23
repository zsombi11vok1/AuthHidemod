plugins {
    id("fabric-loom") version "1.14.10"
    java
    `maven-publish`
}

version = "1.2.3+mc1.21.11"
group = "com.zsombi11.authhide"

repositories {
    maven {
        url = uri("https://maven.terraformersmc.com/releases")
    }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11")
    mappings("net.fabricmc:yarn:1.21.11+build.6:v2")
    modImplementation("net.fabricmc:fabric-loader:0.18.4")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.141.6+1.21.11")
    modCompileOnly("com.terraformersmc:modmenu:17.0.1")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
