# Use a lightweight Node image
FROM node:20-alpine

# Set working directory inside container
WORKDIR /app

# Copy package files first (better for caching)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the project files
COPY . .

RUN chown node:node /app/data.xml \
    && chmod 664 /app/data.xml

# Make public read-only and owned by root
RUN chown -R root:root /app/public \
    && chmod -R a-w /app/public

# Create a non-root user (node already exists in official image)
RUN chown -R node:node /app/tmp

USER node

# Expose the port your app listens on
EXPOSE 8000

# Start the server
CMD ["node", "server.js"]
