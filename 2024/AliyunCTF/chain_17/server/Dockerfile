FROM openjdk:17-jdk-slim

RUN mkdir /opt/server

COPY server.jar /opt/server
COPY flag /flag
COPY readflag /readflag

RUN chmod 400 /flag
RUN chmod +x /readflag
RUN chmod u+s /readflag

WORKDIR /opt/server

USER nobody
CMD ["java", "-jar", "/opt/server/server.jar"]