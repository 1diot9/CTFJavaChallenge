FROM openjdk:8u342-jre-slim

# 安装 MariaDB 和 Redis
RUN apt-get update && \
    apt-get install -y mariadb-server redis-server && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制Spring Boot jar包
COPY happy_shopping-0.0.1-SNAPSHOT.jar app.jar
COPY database.sql /docker-entrypoint-initdb.d/init.sql

# 设置环境变量
ENV MYSQL_ROOT_PASSWORD=rctf2025mysql
ENV MYSQL_DATABASE=happy_shopping

# 暴露端口
EXPOSE 8086 3306 6379

# 启动脚本
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
