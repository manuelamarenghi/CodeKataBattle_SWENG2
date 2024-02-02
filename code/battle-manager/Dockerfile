FROM openjdk:21

ENV TZ=Europe/Rome

COPY target/*.jar battle-manager-app.jar

ENTRYPOINT ["java","-jar","/battle-manager-app.jar"]

EXPOSE 8082
