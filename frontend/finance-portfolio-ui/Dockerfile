# ---------------------------
# Stage 1: Build Angular app
# ---------------------------
FROM node:20-alpine AS build

WORKDIR /app

# Install dependencies first (for better caching)
COPY package*.json ./
RUN npm ci

# Copy rest of the code and build
COPY . .
RUN npm run build
#RUN npm run build --configuration production

# ---------------------------
# Stage 2: Run with Express
# ---------------------------
FROM node:20-alpine AS runtime

WORKDIR /app

# Install only runtime dependencies
COPY package*.json ./
RUN npm ci --omit=dev

# Copy Angular dist and server.js
COPY --from=build /app/dist ./dist
COPY server.js ./server.js

# Expose port for Docker
EXPOSE 4200

# Start the Express server
CMD ["node", "server.js"]
