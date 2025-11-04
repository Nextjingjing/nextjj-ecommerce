# ================================
# Stage 1: Build
# ================================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy all source files and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ================================
# Stage 2: Run
# ================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the app port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
