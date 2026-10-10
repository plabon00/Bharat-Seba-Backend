# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies
# This caches the dependencies layer so subsequent builds are faster if pom.xml doesn't change
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the production image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR file from the build stage
# The name matches the artifactId and version from your pom.xml
COPY --from=build /app/target/learning-portal-0.0.1-SNAPSHOT.jar app.jar

# Render.com automatically assigns a dynamic port using the PORT environment variable.
# We default to 8080 if it's not set.
ENV PORT=8080
EXPOSE ${PORT}

# Run the application, telling Spring Boot to use the Render-assigned port
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
