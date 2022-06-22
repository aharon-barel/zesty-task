import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  application
  kotlin("jvm")
}

group = "io.zesty"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

apply {
  plugin("kotlin")
  plugin("application")
}

dependencies {
  // kotlin coroutines
  val kotlinCoroutinesVersion: String by project
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

  // vertx
  val vertxVersion: String by project
  implementation("io.vertx:vertx-core:$vertxVersion")

  // kotlin integration with JDK8 CompletableFuture
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

  // aws sdk
  val awsSdkVersion: String by project
  implementation("software.amazon.awssdk:ec2:$awsSdkVersion")

  // jackson
  val jacksonVersion: String by project
  implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

  // logging
  val log4jVersion: String by project
  implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
  val kotlinLoggingVersion: String by project
  implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}

tasks {
  val javaVersion: String by project
  withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = javaVersion
    }
  }

  withType<JavaCompile> {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    options.encoding = "UTF-8"
  }

  application {
    mainClassName = "io.zesty.MainKt"
  }
}
