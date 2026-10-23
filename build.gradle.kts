plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    java
    `maven-publish`
}

version = "1.2.2"
group = "com.zsombi11.authhide"

repositories {
    maven {
        url = uri("https://maven.terraformersmc.com/releases")
    }
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:26.2")
    implementation("net.fabricmc:fabric-loader:0.19.5")
    implementation("net.fabricmc.fabric-api:fabric-api:0.160.0+26.2")
    compileOnly("com.terraformersmc:modmenu:20.0.2")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}
