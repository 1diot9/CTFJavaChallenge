#!/bin/bash
set -e

echo "🔧 Starting MariaDB..."
service mariadb start

# 等待数据库启动
until mysqladmin ping --silent; do
    echo "⏳ Waiting for MariaDB..."
    sleep 1
done

# 初始化数据库
if [ -f /docker-entrypoint-initdb.d/init.sql ]; then
    echo "🛠️ Importing initial database..."
    mysql -uroot -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '${MYSQL_ROOT_PASSWORD}';"
    mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    mysql -uroot -p${MYSQL_ROOT_PASSWORD} ${MYSQL_DATABASE} < /docker-entrypoint-initdb.d/init.sql
    rm -f /docker-entrypoint-initdb.d/init.sql
fi

# 启动 Redis
echo "🚀 Starting Redis..."
service redis-server start

# 启动 Spring Boot
echo "🚀 Starting Spring Boot Application..."
exec java -jar app.jar
