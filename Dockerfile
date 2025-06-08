# Frontend stage
FROM node:20 AS frontend
WORKDIR /app
COPY frontend/ .
RUN npm install && npm run build
RUN ls -la dist

# Backend stage
FROM gradle:8.5-jdk17 AS builder
COPY --chown=gradle:gradle . /app
WORKDIR /app
COPY --from=frontend /app/dist/ /app/src/main/webapp/
RUN chmod +x gradlew && ./gradlew clean build -x test

# Final image running Spring Boot JAR directly
FROM openjdk:17
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080