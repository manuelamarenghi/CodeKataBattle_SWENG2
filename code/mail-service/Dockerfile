FROM openjdk:21

ENV TZ=Europe/Rome

COPY target/*.jar mail-service-app.jar

ENTRYPOINT ["java","-jar","/mail-service-app.jar"]

EXPOSE 8085
