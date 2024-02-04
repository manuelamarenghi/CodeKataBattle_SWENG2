FROM openjdk:21

ENV TZ=Europe/Rome

COPY target/*.jar account-manager-app.jar

ENTRYPOINT ["java","-jar","/account-manager-app.jar"]

EXPOSE 8086