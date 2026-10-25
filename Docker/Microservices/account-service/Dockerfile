FROM eclipse-temurin:21-jre
RUN mkdir /microservices
COPY ./account-service-0.0.1-SNAPSHOT.jar /microservices
EXPOSE 8082
VOLUME csbmicroservices
CMD ["java","-jar","/microservices/account-service-0.0.1-SNAPSHOT.jar"]
