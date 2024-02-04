FROM openjdk:21-jdk-buster

ENV TZ=Europe/Rome

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y unzip

COPY target/*.jar battle-manager-app.jar

ENTRYPOINT ["java","-jar","/battle-manager-app.jar"]

EXPOSE 8082
