FROM golang:1.22-alpine AS builder

COPY ./files/readflag.go /build/

WORKDIR /build/

RUN go build -ldflags '-s -w' readflag.go

FROM openjdk:8-jdk-slim

RUN groupadd -r ctf && \
    useradd -m -r -g ctf ctf && \
    mkdir /app/ && \
    chown -R ctf:ctf /app/

COPY --from=builder --chmod=755 /build/readflag /
COPY --chmod=755 ./files/docker-entrypoint.sh /docker-entrypoint.sh
COPY ./files/EasyDB.jar /app/

EXPOSE 8000

ENTRYPOINT [ "/docker-entrypoint.sh" ]
