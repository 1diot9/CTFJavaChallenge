FROM openjdk:21-jdk-buster

# 制作者信息
LABEL auther_template="Randark_JMT"

COPY ./src /app
COPY ./service/docker-entrypoint.sh /

# 开放的端口取决于服务，一般为8080
EXPOSE 8090
ENTRYPOINT [ "/bin/bash","/docker-entrypoint.sh" ]