# Extension: container image of the application.
# Two stages: the first builds the jar with Maven, the second only runs it,
# so the final image contains a JRE and the jar but no Maven or source code.

# ---------- 1. build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Download the dependencies first: this layer is reused while only the source changes
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
# The tests need a MySQL server, so they are run outside Docker (mvn test)
RUN mvn -B -q package -DskipTests

# ---------- 2. run ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
# Run as a normal user, not root
RUN useradd --system --create-home hospital
USER hospital
COPY --from=build /app/target/hospital-management-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
