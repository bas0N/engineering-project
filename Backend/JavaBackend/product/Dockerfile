# Use an official Java runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/product-0.0.1-SNAPSHOT.jar product-service.jar

# Expose the port used by the application
EXPOSE 8999

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "product-service.jar"]