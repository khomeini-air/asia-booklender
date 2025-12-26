# Use a lightweight JDK image
FROM eclipse-temurin:17-jdk-focal
WORKDIR /app

# 1. Copy the Gradle Wrapper files
# This allows us to use ./gradlew without installing Gradle globally
COPY gradlew .
COPY gradle ./gradle

# 2. Copy the build configuration files
COPY build.gradle settings.gradle ./

# 3. Download dependencies (Cache Layer)
# We run 'help' or 'dependencies' just to force the wrapper to download 
# the Gradle distribution and project dependencies.
RUN ./gradlew --no-daemon dependencies

# 4. Copy the actual source code
COPY src ./src

# 5. Build the application (optional, but recommended to verify)
RUN ./gradlew clean build -x test

# 6. Run the application
# Using --no-daemon is important in Docker to prevent orphan processes
CMD ["./gradlew", "bootRun", "--no-daemon"]
