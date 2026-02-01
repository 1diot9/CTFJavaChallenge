FROM node:18-alpine

# Create app directory
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install --production

# Copy the rest of the app (public, images, server.js, flag.txt, etc.)
COPY . .


# Expose port
EXPOSE 8000

# Start the app
CMD ["node", "server.js"]
