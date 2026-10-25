FROM eclipse-temurin:21-jre
RUN mkdir /microservices
COPY ./customer-service-0.0.1-SNAPSHOT.jar /microservices
EXPOSE 8081
VOLUME csbmicroservices
CMD ["java","-jar","/microservices/customer-service-0.0.1-SNAPSHOT.jar"]
