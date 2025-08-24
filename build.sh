#!/bin/bash

echo "🚀 Building and running Pragma Auth Service"

# Build the application
echo "📦 Building application..."
./gradlew clean build -x test

# Copy JAR to deployment directory
echo "📋 Copying JAR file..."
mkdir -p deployment
cp applications/app-service/build/libs/AuthService.jar deployment/AuthService.jar

# Run with Docker Compose
echo "🐳 Starting services with Docker Compose..."
docker-compose up --build -d

echo "✅ Services started!"