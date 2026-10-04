plugins {
    kotlin("jvm")
    application
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":core"))
    implementation(project(":cli-common"))
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.8")
}