package io.zesty.util

import io.vertx.core.json.JsonObject
import io.zesty.extensions.toJson
import software.amazon.awssdk.regions.Region
import java.nio.file.Path

/**
 * @author Aharon Bar-El 22/06/2022
 */
object FileSystemUtil {
  private const val REGIONS_FILE_PATH = "src/main/resources/regions.txt"
  private const val CONFIG_FILE_PATH = "src/main/resources/config.json"

  fun readConfigFile(): JsonObject = readFileContent(CONFIG_FILE_PATH).toJson()

  fun readRegionsInputFile(): Set<Region> {
    return readFileContent(REGIONS_FILE_PATH)
      .split(",")
      .map {
        val currentRegion = Region.of(it.trim())
        if (!Region.regions().contains(currentRegion)) {
          error("'$currentRegion' is not a valid AWS region. Please check the input file located at $REGIONS_FILE_PATH")
        }
        currentRegion
      }
      .toSet()
  }

  fun readFileContent(path: String) = Path.of(path)
    .toFile()
    .let {
      if (it.exists()) {
        it.readText()
      } else {
        error("File in path '$path' does not exist")
      }
    }
}
