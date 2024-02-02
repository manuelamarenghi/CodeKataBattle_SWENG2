FROM openjdk:21-jdk-buster

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y git && \
    apt-get install -y cppcheck && \
    apt-get install -y gcc

COPY target/*.jar solution-evaluation-service-app.jar

ENTRYPOINT ["java","-jar","/solution-evaluation-service-app.jar"]

EXPOSE 8081