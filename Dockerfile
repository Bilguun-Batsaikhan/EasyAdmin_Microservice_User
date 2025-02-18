# Use a multi-stage build
FROM eclipse-temurin:17-jdk AS build

# Set working directory inside the container
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src

# Grant permission to execute Maven wrapper
RUN chmod +x mvnw

# Build the application (creates the JAR file)
RUN ./mvnw clean package -DskipTests

# Use a new image for running the application
FROM eclipse-temurin:17-jdk AS runtime

WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
