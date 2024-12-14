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
    implementation(group = "org.openjfx", name = "javafx-base", version = "23", classifier = platform)
    implementation(group = "org.openjfx", name = "javafx-graphics", version = "23", classifier = platform)
    implementation(group = "org.openjfx", name = "javafx-controls", version = "23", classifier = platform)
    implementation(group = "org.openjfx", name = "javafx-fxml", version = "23", classifier = platform)
    implementation(group = "org.openjfx", name = "javafx-web", version = "23", classifier = platform)
    implementation("org.controlsfx:controlsfx:11.1.1")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0")
    implementation("net.synedra:validatorfx:0.5.0")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    implementation("eu.hansolo:tilesfx:21.0.3")
    implementation("com.github.almasb:fxgl:17.3")



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