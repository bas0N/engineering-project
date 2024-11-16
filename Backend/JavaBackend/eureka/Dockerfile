# Use an official Java runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/eureka-0.0.1-SNAPSHOT.jar eureka.jar

# Expose the port used by the application
EXPOSE 8761

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "eureka.jar"]
