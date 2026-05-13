# Docker Deployment Guide - Pet Adoption System

## Quick Start

```bash
# Navigate to project root
cd /Users/nella/Downloads/PetAdoptionSystem

# Build and start all containers
docker-compose up --build

# Access the application
# Frontend: http://localhost:5173
# Backend: http://localhost:8082
```

## What's Running

1. **Frontend** (Port 5173) - React app served with Node.js
2. **Backend** (Port 8082) - Spring Boot API with H2 database

## Docker Commands

```bash
# Start services
docker-compose up

# Start in background
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs

# Rebuild
docker-compose up --build
```

## For Your Exam

**Tell your examiner:**
"The application is dockerized with 2 containers - frontend and backend. You can run the entire system with one command: `docker-compose up`"

That's it! Simple and gets you the 5 marks for dockerization.
