plugins {
    kotlin("jvm") version "1.4.21"
}

group = "com.sanastasov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
    jcenter()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    val arrow_version = "latest.integration"
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("io.arrow-kt:arrow-syntax:$arrow_version")
    implementation("io.arrow-kt:arrow-fx:$arrow_version")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrow_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("io.github.microutils:kotlin-logging:1.7.7")
    implementation("org.slf4j:slf4j-simple:1.7.26")

    val kotest = "4.3.1"
    testImplementation("io.kotest:kotest-runner-junit5:$kotest") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core:$kotest") // for kotest core jvm assertions
    testImplementation("com.github.kirviq:dumbster:1.7.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
