# Frontend stage
FROM node:20 AS frontend
WORKDIR /app
COPY frontend/ .
RUN npm install && npm run build

# Backend stage
FROM gradle:8.5-jdk17 AS builder
COPY --chown=gradle:gradle . /app
WORKDIR /app
COPY --from=frontend /app/dist/ src/main/resources/static/
RUN ./gradlew clean build -x test

# Final image with Tomcat
FROM tomcat:9.0-jdk17
COPY --from=builder /app/build/libs/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080