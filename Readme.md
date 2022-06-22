# Description
This is a simple kotlin script that interacts with AWS ec2 endpoint

# Prerequisites

- Gradle 7.4.2
- Java 11

# How To Run

1. docker run --rm -d --name zesty-ec2 -p 4000:4000 zestyco/ec2-challenge
2. ./gradlew clean run

# Notes
You can expand logger level to debug by changing `level="debug"` in the `zesty-task/src/main/resources/log4j2.xml` file.
This will print the results of reading the region json files to the console when invoking `Ec2Service.readInstancesFromGeneratedFile` function
