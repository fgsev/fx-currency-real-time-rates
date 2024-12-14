import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem

plugins {
    application
    id("pl.allegro.tech.build.axion-release").version("1.13.6")
}

project.version = scmVersion.version

scmVersion {
    localOnly = true
}

val platform = when {
    getCurrentOperatingSystem().isWindows -> "win"
    getCurrentOperatingSystem().isLinux -> "linux"
    getCurrentOperatingSystem().isMacOsX -> "mac"
    else -> throw UnsupportedOperationException("Operating system ${getCurrentOperatingSystem()} not supported yet")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

application {
    mainClass.set("com.fxcurrency.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}