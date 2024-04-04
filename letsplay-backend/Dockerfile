FROM maven:3.9.6-eclipse-temurin-21 AS maven-builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jdk
WORKDIR /app
EXPOSE 8080
COPY --from=maven-builder /app/target/*.jar ./app.jar
COPY src/main/resources/application-prod.properties /app/application.properties
CMD ["java", "-jar", "app.jar"]
