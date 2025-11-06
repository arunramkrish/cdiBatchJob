#!/bin/bash

# Script to stop all Docker services for CDI Batch Job

echo "Stopping Docker services..."

docker-compose down

echo "Services stopped."

