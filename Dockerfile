FROM tomcat:9.0-jdk17
LABEL maintainer="jan.divis"

RUN rm -rf /usr/local/tomcat/webapps/*

COPY build/libs/Krypto_navazujici-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080