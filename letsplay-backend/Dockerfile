FROM maven:3.9.6-eclipse-temurin-21 AS maven-builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jdk
WORKDIR /app
EXPOSE 8080
COPY --from=maven-builder /app/target/*.jar ./app.jar
#copy src/main/resources/application.properties-docker /app/application.properties
CMD ["java", "-jar", "-dspring.profiles.active=docker", "app.jar"]
