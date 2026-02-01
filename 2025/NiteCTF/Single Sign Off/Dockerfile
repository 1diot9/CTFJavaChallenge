FROM ubuntu:23.04

ENV DEBIAN_FRONTEND=noninteractive

ARG NITE_USER
ARG NITE_PASSWORD

RUN sed -i 's/archive.ubuntu.com/old-releases.ubuntu.com/g' /etc/apt/sources.list && \
    sed -i 's/security.ubuntu.com/old-releases.ubuntu.com/g' /etc/apt/sources.list

RUN apt-get update && apt-get install -y \
    build-essential \
    wget \
    libssl-dev \
    libcurl4-openssl-dev \
    python3 \
    python3-pip \
    nginx \
    supervisor \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt .
RUN pip3 install --no-cache-dir --break-system-packages -r requirements.txt

COPY document-portal /app/document-portal
WORKDIR /app/document-portal
RUN gcc -o fetcher fetcher.c -lcurl -O2


RUN echo 'machine gitlab.internal.corp' > /root/.netrc && \
    echo '  login devuser' >> /root/.netrc && \
    echo '  password DevPass2024!' >> /root/.netrc && \
    echo '' >> /root/.netrc && \
    echo 'machine nite-sso' >> /root/.netrc && \
    echo "  login ${NITE_USER}" >> /root/.netrc && \
    echo "  password ${NITE_PASSWORD}" >> /root/.netrc && \
    echo '' >> /root/.netrc && \
    echo 'machine jenkins.internal.corp' >> /root/.netrc && \
    echo '  login jenkins-bot' >> /root/.netrc && \
    echo '  password AutomationKey789' >> /root/.netrc && \
    echo 'default' >> /root/.netrc && \
    chmod 600 /root/.netrc

WORKDIR /app
COPY nite-vault /app/nite-vault

COPY nite-sso /app/nite-sso

COPY nginx.conf /etc/nginx/nginx.conf
COPY supervisord.conf /app/supervisord.conf
COPY init.sh /app/init.sh
COPY start.sh /app/start.sh

RUN mkdir -p /var/log/nginx /var/lib/nginx /var/cache/nginx /run/nginx && \
    chmod +x /app/init.sh /app/start.sh

ENV DOMAIN=single-sign-off.chals.live
ENV DOCUMENT_PORTAL_PORT=10000
ENV NITE_VAULT_PORT=5000
ENV NITE_SSO_PORT=8989
ENV FLAG=nite{fake_flag}
ENV NITE_USER=${NITE_USER}
ENV NITE_PASSWORD=${NITE_PASSWORD}
ENV SECRET_KEY=secret-key-lol

ENV SSO_EXTERNAL_URL=http://nite-sso.${DOMAIN}
ENV PORTAL_EXTERNAL_URL=http://document-portal.${DOMAIN}

EXPOSE 80

CMD ["bash", "/app/init.sh"]

    



