# Use an official Java runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/order-0.0.1-SNAPSHOT.jar order-service.jar

# Expose the port used by the application
EXPOSE 9779

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "order-service.jar"]




