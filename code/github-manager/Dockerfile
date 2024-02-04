FROM openjdk:21

ENV TZ=Europe/Rome

COPY target/*.jar github-manager-app.jar

ENTRYPOINT ["java","-jar","/github-manager-app.jar"]

EXPOSE 8083
