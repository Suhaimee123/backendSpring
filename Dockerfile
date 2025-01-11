# Stage 1: Build Stage
FROM maven:3.9.5-eclipse-temurin-17 AS builder
USER root

# Copy the project files into the container
WORKDIR /app
COPY . .

# Build the application, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Runtime Stage
FROM eclipse-temurin:17.0.4.1_1-jre-jammy
USER root

# Create application directory
RUN mkdir /opt/app-root
WORKDIR /opt/app-root
RUN chmod g+w /opt/app-root

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Copy googledrive.json to the application directory
COPY googledrive.json /opt/app-root/googledrive.json

# Set environment variable for Google API
ENV GOOGLE_APPLICATION_CREDENTIALS=/opt/app-root/googledrive.json

# Expose the application's port
EXPOSE 8089

# Set timezone
ENV TZ="Asia/Bangkok"

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
