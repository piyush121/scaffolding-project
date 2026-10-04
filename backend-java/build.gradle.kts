plugins {
    java
    application
}

group = "com.scaffolding"
version = "0.0.1-SNAPSHOT"

java {
    // Compile for Java 17 with whatever JDK (17+) is running Gradle.
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.release.set(17)
}

application {
    mainClass.set("com.scaffolding.Application")
}

repositories {
    mavenCentral()
}

dependencies {
    // Servlet container
    implementation("org.apache.tomcat.embed:tomcat-embed-core:10.1.39")
    // REST layer (JAX-RS) with JSON via Jackson
    implementation("org.glassfish.jersey.containers:jersey-container-servlet:3.1.10")
    implementation("org.glassfish.jersey.inject:jersey-hk2:3.1.10")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.1.10")
    // DAO layer
    implementation("org.mongodb:mongodb-driver-sync:5.2.1")
    implementation("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.17")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
