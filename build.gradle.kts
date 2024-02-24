plugins {
    kotlin("jvm") version "1.9.21"
    application
}

group = "com.github.feeling"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.20")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:2.0.0-Beta2")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
    implementation("org.json:json:20240205")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")
    implementation("io.ktor:ktor-server-core:2.3.8")
    implementation("io.ktor:ktor-client-apache5:2.3.8")
    implementation("io.ktor:ktor-client-okhttp:2.3.8")
    implementation("com.aallam.openai:openai-client:3.7.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.github.feeling.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}