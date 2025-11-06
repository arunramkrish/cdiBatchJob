#!/bin/bash

# Script to stop and remove all Docker services and volumes

read -p "This will remove all data volumes. Are you sure? (y/N) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "Stopping and removing services and volumes..."
    docker-compose down -v
    echo "All services and data volumes removed."
else
    echo "Cancelled."
fi

