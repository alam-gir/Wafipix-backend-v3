# ---- Build Stage ----
FROM sapmachine:24-jdk-ubuntu-noble AS builder

WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy pom.xml first to leverage Docker layer caching
COPY pom.xml .

# Download dependencies (go-offline speeds up subsequent builds)
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src src

# Build the executable JAR file
RUN mvn package -DskipTests

# ---- Run Stage ----
FROM sapmachine:24-jre-ubuntu-noble AS runner

# Define the JAR file name as an argument
ARG JAR_FILE=target/*.jar
ENV PORT=8080
EXPOSE ${PORT}

# Create a non-root user for security best practices
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring

# Copy the application JAR from the 'builder' stage
COPY --from=builder /app/${JAR_FILE} app.jar

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
