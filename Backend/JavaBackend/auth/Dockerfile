# Use an official Java runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/auth-0.0.1-SNAPSHOT.jar auth-service.jar

# Expose the port used by the application
EXPOSE 9898

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "auth-service.jar"]




