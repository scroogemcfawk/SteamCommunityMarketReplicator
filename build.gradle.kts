group = "RePricer.dbm"
version = "0.0.0"

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.8.10"
}

dependencies {
    implementation("org.json:json:20230227")
    implementation("net.sf.json-lib:json-lib:2.4:jdk15")
}
