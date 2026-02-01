FROM node:16-alpine3.16

ENV LANG="C.UTF-8" PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true

# Install dependencies and set up the user
RUN apk update && \
    apk add --no-cache zlib-dev udev nss ca-certificates chromium && \
    adduser -h /home/bot -D -u 10086 bot && \
    yarn cache clean && \
    rm -rf /tmp/* /etc/apk/* /var/cache/apk/* /usr/share/man

WORKDIR /home/bot

# Copy package.json and install dependencies
COPY ./*.json .
RUN npm install

# Copy the rest of the application files
COPY . .

# Switch to the non-root user
USER bot

EXPOSE 9000

CMD ["node", "/home/bot/index.js"]
