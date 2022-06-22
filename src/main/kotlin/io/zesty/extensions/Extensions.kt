package io.zesty.extensions

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.nio.file.Path

/**
 * @author Aharon Bar-El 22/06/2022
 */
fun String.toJson() = JsonObject(this)

fun JsonArray.toFile(path: String) = Path.of(path).toFile()
  .let { file ->
    if (!file.exists()) {
      file.parentFile.mkdirs()
      file.createNewFile()
    }
    file.writeText(this.encodePrettily())
  }
