plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":core"))
    implementation("org.jline:jline:3.26.1")
    implementation("org.jline:jline-terminal-jna:3.26.1")
    implementation("net.java.dev.jna:jna:5.14.0")
    implementation(libs.kotlinx.immutable)
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}
