# Stage 1: Build frontend
FROM node:20 AS frontend
WORKDIR /app
COPY frontend/ .
RUN npm install && npm run build

# Stage 2: Build Spring Boot JAR with embedded frontend
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN mkdir -p src/main/webapp/
COPY --from=frontend /app/dist/ /app/src/main/webapp/
RUN gradle clean build -x test

# Stage 3: Run the Spring Boot app
FROM openjdk:17-jdk-slim
WORKDIR /app
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=50.0 -XX:+ExitOnOutOfMemoryError"
COPY --from=builder /app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]