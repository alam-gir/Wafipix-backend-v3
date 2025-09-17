# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy source code and pom.xml
COPY pom.xml .
COPY src ./src

# Build the JAR file
RUN mvn clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:21-jre as run

WORKDIR /app

# Copy only the JAR file from the build stage
COPY --from=build /app/target/wafipix-0.0.1-SNAPSHOT.jar ./wafipix.jar

# Optionally copy .env if needed
COPY .env .env

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "wafipix.jar"]
