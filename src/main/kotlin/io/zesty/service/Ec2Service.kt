package io.zesty.service

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.zesty.config.ConfigurationUtils.extractAwsAccessKey
import io.zesty.config.ConfigurationUtils.extractAwsEc2Endpoint
import io.zesty.config.ConfigurationUtils.extractAwsSecretKey
import io.zesty.extensions.toFile
import io.zesty.util.FileSystemUtil.readFileContent
import io.zesty.util.FileSystemUtil.readRegionsInputFile
import mu.KLogging
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest
import software.amazon.awssdk.services.ec2.model.Instance
import java.net.URI
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant

/**
 * @author Aharon Bar-El 22/06/2022
 */
object Ec2Service : KLogging() {
  private lateinit var config: JsonObject
  private const val TARGET_STORE_PREFIX = "src/main/resources/results"
  private val mapper: ObjectMapper by lazy {
    ObjectMapper().apply {
      this.registerModule(JavaTimeModule())
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .setDateFormat(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
    }
  }

  fun init(config: JsonObject) {
    this.config = config
    val selectedRegions = readRegionsInputFile()

    selectedRegions
      .associateWith { region -> getAllEc2InstancesByRegion(region) } // create map Region -> List<Instance>
      .asSequence()
      .forEach {
        val path = "$TARGET_STORE_PREFIX/${it.key}.json"
        val instancesAsString = mapper.writeValueAsString(it.value)
        JsonArray(instancesAsString).toFile(path)
      }
  }

  private fun getAllEc2InstancesByRegion(region: Region): List<Instance> {
    val ec2ClientByRegion = buildEc2ClientByRegion(region)
    val ec2Instances = mutableListOf<Instance>()
    var nextToken: String?

    do {
      val describeInstancesRequest = DescribeInstancesRequest.builder().build()
      val describeInstancesResponse = ec2ClientByRegion.describeInstances(describeInstancesRequest)

      describeInstancesResponse.reservations()
        .flatMap { it.instances() }
        .sortedWith(compareBy { it.launchTime() })
        .forEach {
          val numberOfRunningDays = Duration.between(it.launchTime(), Instant.now()).toSeconds()
          logger.info { "Instance with id [${it.instanceId()}], region [${region}], is running for $numberOfRunningDays seconds" }
          ec2Instances.add(it)
        }

      nextToken = describeInstancesResponse.nextToken()

    } while (nextToken != null)

    return ec2Instances
  }

  fun readInstancesFromGeneratedFile(region: Region): JsonArray {
    val path = "$TARGET_STORE_PREFIX/$region.json"
    return JsonArray(readFileContent(path)).also { logger.debug { it.encodePrettily() } }
  }

  private fun buildEc2ClientByRegion(region: Region): Ec2Client = Ec2Client.builder()
    .credentialsProvider(
      StaticCredentialsProvider.create(
        AwsBasicCredentials.create(
          extractAwsAccessKey(config),
          extractAwsSecretKey(config)
        )
      )
    )
    .endpointOverride(URI.create(extractAwsEc2Endpoint(config)))
    .region(region)
    .build()
}
