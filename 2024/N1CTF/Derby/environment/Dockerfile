FROM openjdk:17-jdk-slim

COPY --chmod=755 ./files/flag.sh /flag.sh

COPY --chmod=755 ./files/docker-entrypoint.sh /docker-entrypoint.sh

COPY --chmod=755 ./files/readflag /readflag

COPY ./src/ /app/

EXPOSE 8080

ENTRYPOINT [ "/docker-entrypoint.sh" ]