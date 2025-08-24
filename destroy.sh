#!/bin/bash

echo "🛑 Stopping and cleaning Pragma Auth Service"

# Stop containers and remove volumes
echo "📦 Stopping containers and removing volumes..."
docker-compose down -v

echo "✅ Cleanup completed!"
