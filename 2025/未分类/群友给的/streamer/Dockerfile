FROM openjdk:17.0.2-slim

WORKDIR /opt

COPY ./_files/start.sh /root/start.sh
COPY ./src/ /app/

RUN apt-get update && \
    apt-get install -y wget tar xdg-utils w3m && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    chmod 755 /root/start.sh 


RUN wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.109/bin/apache-tomcat-9.0.109.tar.gz && \
    tar -xzf apache-tomcat-9.0.109.tar.gz && \
    mv apache-tomcat-9.0.109 tomcat && \
    rm apache-tomcat-9.0.109.tar.gz


ENTRYPOINT [ "/root/start.sh" ]
