# 1. Build Vue frontend
FROM node:20-alpine AS frontend
WORKDIR /app
COPY frontend/ .
RUN npm install && npm run build

# 2. Build Spring WAR with frontend content
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /app
COPY --from=frontend /app/dist /app/src/main/webapp/
WORKDIR /app
RUN gradle clean war

# 3. Deploy WAR to Tomcat
FROM tomcat:9.0-jdk17
COPY --from=build /app/build/libs/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080