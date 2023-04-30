group = "RePricer.dbm"
version = "0.0.3"

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.8.10"
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("org.json:json:20230227")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.github.albfernandez:juniversalchardet:2.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
}
