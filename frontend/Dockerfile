# Frontend Dockerfile for Pet Adoption System - Simple Version
# Stage 1: Build the React application
FROM node:20-alpine AS build

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy source code and build
COPY . .
RUN npm run build

# Stage 2: Serve with Node (simpler than Nginx)
FROM node:20-alpine

WORKDIR /app

# Install serve package to serve static files
RUN npm install -g serve

# Copy built files from build stage
COPY --from=build /app/dist ./dist

# Expose port 5173 (Vite's default)
EXPOSE 5173

# Serve the application
CMD ["serve", "-s", "dist", "-l", "5173"]
