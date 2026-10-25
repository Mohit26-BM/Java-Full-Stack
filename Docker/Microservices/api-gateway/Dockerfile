FROM eclipse-temurin:21-jre
RUN mkdir /microservices
COPY ./api-gateway-0.0.1-SNAPSHOT.jar /microservices
EXPOSE 8765
VOLUME csbmicroservices
CMD ["java","-jar","/microservices/api-gateway-0.0.1-SNAPSHOT.jar"]
