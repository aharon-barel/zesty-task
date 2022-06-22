import java.util.Properties

plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  mavenLocal()
  gradlePluginPortal()
}

Properties().apply {
  val props = this
  rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().apply {
    val file = this
    file.inputStream().use {
      props.load(it)
    }
  }
}.forEach { key, value -> project.extra.set(key as String, value) }

dependencies {
  val kotlinVersion: String by project
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}
