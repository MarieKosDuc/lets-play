from maven:3.9.6-eclipse-temurin-21 as maven-builder
workdir /app
copy pom.xml .
copy src ./src
run mvn clean package -DskipTests

from eclipse-temurin:21-jdk
workdir /app
expose 8080
copy --from=maven-builder /app/target/*.jar ./app.jar
cmd ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
