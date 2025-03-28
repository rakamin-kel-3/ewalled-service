# Use OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy built JAR file
COPY target/ewalled.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
