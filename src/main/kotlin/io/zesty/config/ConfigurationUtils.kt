package io.zesty.config

import io.vertx.core.json.JsonObject

/**
 * @author Aharon Bar-El 22/06/2022
 */
object ConfigurationUtils {
  private const val AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID"
  private const val AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY"
  private const val AWS_EC2_ENDPOINT = "AWS_EC2_ENDPOINT"

  private fun extractAwsConfig(config: JsonObject): JsonObject = config.getJsonObject("aws", JsonObject())

  fun extractAwsAccessKey(config: JsonObject): String = System.getenv(AWS_ACCESS_KEY_ID)
    ?: extractAwsConfig(config).getString("accessKey")
    ?: error("aws access key not provided")

  fun extractAwsSecretKey(config: JsonObject): String = System.getenv(AWS_SECRET_ACCESS_KEY)
    ?: extractAwsConfig(config).getString("secretKey")
    ?: error("aws secret key not provided")


  fun extractAwsEc2Endpoint(config: JsonObject): String = System.getenv(AWS_EC2_ENDPOINT)
    ?: extractAwsConfig(config).getString("endpoint")
    ?: error("aws ec2 endpoint not provided")
}
