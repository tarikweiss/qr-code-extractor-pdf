val pdfBoxVersion = "3.0.3"
val zxingVersion = "3.5.3"
val kotlinJsonVersion = "1.7.3"

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
}
group = "de.tarikweiss"
version = "1.0-SNAPSHOT"
repositories {
    mavenCentral()
}
dependencies {
    testImplementation(kotlin("test"))

    // https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox
    implementation("org.apache.pdfbox:pdfbox:$pdfBoxVersion")

    // https://mvnrepository.com/artifact/com.google.zxing/core
    implementation("com.google.zxing:core:$zxingVersion")

    // https://mvnrepository.com/artifact/com.google.zxing/javase
    implementation("com.google.zxing:javase:$zxingVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinJsonVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}