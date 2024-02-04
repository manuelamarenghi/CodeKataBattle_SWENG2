FROM openjdk:21

ENV TZ=Europe/Rome

COPY target/*.jar api-gateway-app.jar

ENTRYPOINT ["java","-jar","/api-gateway-app.jar"]

EXPOSE 8080