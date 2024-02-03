FROM openjdk:21

ENV TZ=Europe/Rome

COPY target/*.jar discovery-server-app.jar

ENTRYPOINT ["java","-jar","/discovery-server-app.jar"]

EXPOSE 8761