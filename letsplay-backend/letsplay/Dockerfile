from maven:3.9.6-eclipse-temurin-21 as maven-builder
workdir /app
copy . /app
run mvn clean install

from eclipse-temurin:21-jdk
workdir /app
expose 8080
copy target/*.jar app.jar
#copy src/main/resources/application.properties-docker /app/application.properties
entrypoint ["java", "-jar", "-dspring.profiles.active=docker", "app.jar"]
