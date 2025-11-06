#!/bin/bash

# Script to start all Docker services for CDI Batch Job

echo "Starting Docker services for CDI Batch Job..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker Desktop."
    exit 1
fi

# Start services
docker-compose up -d

echo "Waiting for services to be healthy..."
sleep 10

# Check PostgreSQL
if docker exec cdi-batch-postgres pg_isready -U postgres > /dev/null 2>&1; then
    echo "✓ PostgreSQL is ready"
else
    echo "✗ PostgreSQL is not ready"
fi

# Check MongoDB
if docker exec cdi-batch-mongodb mongosh --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
    echo "✓ MongoDB is ready"
else
    echo "✗ MongoDB is not ready"
fi

# Check Elasticsearch
if curl -s http://localhost:9200/_cluster/health > /dev/null 2>&1; then
    echo "✓ Elasticsearch is ready"
else
    echo "✗ Elasticsearch is not ready (may take 30-60 seconds to start)"
fi

echo ""
echo "Services started. You can now run the Spring Boot application."
echo "Use 'docker-compose logs -f' to view logs"
echo "Use 'docker-compose down' to stop services"

