FROM openjdk:11-jre-slim-buster
COPY target/*.jar smart-hardware-shop.jar
ENTRYPOINT ["java","-jar","/smart-hardware-shop.jar"]