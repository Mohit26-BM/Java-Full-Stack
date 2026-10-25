FROM eclipse-temurin:21-jre
RUN mkdir /microservices
COPY ./eureka-server-0.0.1-SNAPSHOT.jar /microservices
EXPOSE 8761
VOLUME csbmicroservices
CMD ["java","-jar","/microservices/eureka-server-0.0.1-SNAPSHOT.jar"]
