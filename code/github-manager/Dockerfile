FROM openjdk:21

COPY target/*.jar mail-service-app.jar

ENTRYPOINT ["java","-jar","/mail-service-app.jar"]

EXPOSE 8083
