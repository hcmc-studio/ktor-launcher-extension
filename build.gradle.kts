val project_version: String by project
val jdk_version: String by project
val kotlinx_coroutines_version: String by project
val kotlinx_datetime_version: String by project
val kotlinx_serialization_version: String by project
val hcmc_extension_version: String by project
val ktor_version: String by project
val exposed_version: String by project

plugins {
    kotlin("jvm")
    id("maven-publish")
}

group = "studio.hcmc"
version = project_version

repositories {
    mavenCentral()
    mavenLocal()
    maven { setUrl("https://jitpack.io") }
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(jdk_version.toInt())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "studio.hcmc"
            artifactId = project.name
            version = project_version
            from(components["java"])
        }
        create<MavenPublication>("jitpack") {
            groupId = "com.github.hcmc-studio"
            artifactId = project.name
            version = "$project_version-release"
            from(components["java"])
        }
    }
}

dependencies {
    implementation(fileTree(mapOf(
        "dir" to "/Users/ji-hwankim/Workspace/HCMC/ktor-plugin-content-negotiation/build/libs",
        "include" to "ktor-plugin-content-negotiation-0.0.39.jar"
    )))

    implementation("com.github.hcmc-studio:exposed-table-extension:$hcmc_extension_version")
    implementation("com.github.hcmc-studio:exposed-transaction-extension:$hcmc_extension_version")
    implementation("com.github.hcmc-studio:ktor-plugin-accepted-at:$hcmc_extension_version")
    implementation("com.github.hcmc-studio:ktor-plugin-request-logging:$hcmc_extension_version")
    implementation("com.github.hcmc-studio:ktor-plugin-response-logging:$hcmc_extension_version")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:$kotlinx_datetime_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:$kotlinx_serialization_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:$kotlinx_serialization_version")

    implementation("io.ktor:ktor-server-auto-head-response-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-double-receive-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-resources-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
}