plugins {
    `java-library`
    java
}

group = "org.incendo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

apply<JavaLibraryPlugin>()

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    compileOnly("org.checkerframework", "checker-qual", "3.8.0")
    api("io.leangen.geantyref", "geantyref", "1.3.11")
    testImplementation("org.junit.jupiter", "junit-jupiter-engine", "5.7.0")
}
