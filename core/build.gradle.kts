plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(libs.kotlinx.immutable)
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}
