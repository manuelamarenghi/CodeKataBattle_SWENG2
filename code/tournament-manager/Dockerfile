FROM openjdk:21

ENV TZ=Europe/Rome

COPY target/*.jar tournament-manager-app.jar

ENTRYPOINT ["java","-jar","/tournament-manager-app.jar"]

EXPOSE 8085
