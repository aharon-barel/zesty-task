package io.zesty

import io.vertx.core.json.JsonObject
import io.zesty.service.Ec2Service
import io.zesty.util.FileSystemUtil.readConfigFile
import software.amazon.awssdk.regions.Region

/**
 * @author Aharon Bar-El 22/06/2022
 */
fun main() {
  val config: JsonObject = readConfigFile()
  Ec2Service.init(config)
  Ec2Service.readInstancesFromGeneratedFile(Region.US_EAST_1)
  Ec2Service.readInstancesFromGeneratedFile(Region.EU_WEST_1)
  Ec2Service.readInstancesFromGeneratedFile(Region.AP_SOUTHEAST_1)
}
