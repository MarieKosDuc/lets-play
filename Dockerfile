# Maven install back-end
FROM maven:3.9.6-eclipse-temurin-21 as maven-builder
WORKDIR /app
COPY letsplay-backend/ /app
RUN mvn -f /app/letsplay clean install

# Build back-end
FROM eclipse-temurin:21-jdk 
WORKDIR /app
EXPOSE 8080
COPY --from=maven-builder /app/letsplay/target/*.jar /app/app.jar
COPY letsplay-backend/letsplay/src/main/resources/application.properties-docker /app/application.properties
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]

# Build front-end
FROM node:16-alpine as angular-builder
WORKDIR /app/frontend
COPY letsplay-frontend/ /app/frontend
RUN npm install
RUN npm run build

# nginx serve

FROM nginx:1.21.1-alpine
COPY --from=angular-builder /app/frontend/dist /usr/share/nginx/html
EXPOSE 80