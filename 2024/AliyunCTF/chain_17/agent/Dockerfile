FROM openjdk:17-jdk-slim

RUN mkdir /opt/agent

COPY agent.jar /opt/agent

WORKDIR /opt/agent

CMD ["java", "--add-opens", "java.base/java.util.concurrent.atomic=ALL-UNNAMED", "-jar", "/opt/agent/agent.jar"]