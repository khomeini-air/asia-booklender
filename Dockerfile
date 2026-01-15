# Use a lightweight JDK image
FROM eclipse-temurin:17-jdk-focal
WORKDIR /app

# Copy the Gradle Wrapper files
COPY gradlew .
COPY gradle ./gradle

# Copy the build configuration files
COPY build.gradle settings.gradle ./

# Download dependencies (Cache Layer)
RUN ./gradlew --no-daemon dependencies

# Copy the actual source code
COPY src ./src

# Build the application (optional, but recommended to verify)
RUN ./gradlew clean build -x test

# Run the application
CMD ["./gradlew", "bootRun", "--no-daemon"]
