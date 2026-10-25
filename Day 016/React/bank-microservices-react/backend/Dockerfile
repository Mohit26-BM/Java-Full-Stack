# Shared by all five services. Pick the service with:  --build-arg MODULE=<folder name>
FROM maven:3.9-eclipse-temurin-17 AS build
ARG MODULE
WORKDIR /build
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY customer-service/pom.xml customer-service/pom.xml
COPY account-service/pom.xml account-service/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY ${MODULE}/src ${MODULE}/src
RUN mvn -B -q -pl ${MODULE} -am -DskipTests package

FROM eclipse-temurin:17-jre
ARG MODULE
WORKDIR /app
COPY --from=build /build/${MODULE}/target/${MODULE}-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
