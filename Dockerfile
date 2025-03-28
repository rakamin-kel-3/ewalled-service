# Stage 1: Build the application
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
# Copy the pom file and source code into the container
COPY pom.xml .
COPY src ./src
# Build the application; skip tests for faster builds
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the built JAR from the previous stage
COPY --from=build /app/target/ewalled.jar app.jar
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
