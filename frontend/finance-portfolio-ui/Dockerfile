# ---------------------------
# Stage 1: Build Angular app
# ---------------------------
FROM node:20-alpine AS build

WORKDIR /app

# Install dependencies first (better caching)
COPY package*.json ./
RUN npm ci

# Copy rest of the code and build
COPY . .
RUN npm run build --configuration production

# ---------------------------
# Stage 2: Run with Express
# ---------------------------
FROM node:20-alpine AS runtime

WORKDIR /app

# Copy only what is needed for runtime
COPY package*.json ./
RUN npm ci --omit=dev

# Copy dist output and server.js
COPY --from=build /app/dist ./dist
COPY server.js ./server.js

# Expose port for the container
EXPOSE 4200

# Start the Express server
CMD ["node", "server.js"]
