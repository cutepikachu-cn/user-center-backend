FROM maven:3-jdk-17 as builder

# Copy local code to container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build release artifact.
RUN mvn package -DskipTest

# Run the web service on container startup.
CMD ["java", "-jar", "/app/target/user-center-backend-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]
