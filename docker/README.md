# Docker Setup for CDI Batch Job

This directory contains Docker configuration files to run all dependent services locally.

## Services

1. **PostgreSQL** (Port 5432) - Spring Batch job repository
2. **MongoDB** (Port 27017) - Source database
3. **Elasticsearch** (Port 9200) - Target search engine

## Prerequisites

- Docker Desktop installed and running
- Docker Compose v3.8 or higher

## Quick Start

### Start all services

From the project root directory:

```bash
docker-compose up -d
```

Or use the helper script:

```bash
chmod +x docker/scripts/start-services.sh
./docker/scripts/start-services.sh
```

### Check service status

```bash
docker-compose ps
```

### View logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f postgres
docker-compose logs -f mongodb
docker-compose logs -f elasticsearch
```

### Stop all services

```bash
docker-compose down
```

Or use the helper script:

```bash
./docker/scripts/stop-services.sh
```

### Stop and remove volumes (clean slate)

```bash
docker-compose down -v
```

Or use the helper script:

```bash
./docker/scripts/clean-all.sh
```

## Service Details

### PostgreSQL
- **Database**: `batch_job_repository`
- **Username**: `postgres`
- **Password**: `postgres`
- **Port**: `5432`

### MongoDB
- **Database**: `cdi_database`
- **Collection**: `content`
- **Port**: `27017`

### Elasticsearch
- **Port**: `9200`
- **Security**: Disabled (for local development)
- **Memory**: 512MB heap

## Health Checks

All services include health checks. You can verify they're healthy:

```bash
# PostgreSQL
docker exec cdi-batch-postgres pg_isready -U postgres

# MongoDB
docker exec cdi-batch-mongodb mongosh --eval "db.adminCommand('ping')"

# Elasticsearch
curl http://localhost:9200/_cluster/health
```

## Environment Variables

The application uses these connection details (matching docker-compose.yml):

- `DB_USERNAME=postgres`
- `DB_PASSWORD=postgres`
- `MONGODB_URI=mongodb://localhost:27017/cdi_database`
- `ELASTICSEARCH_SERVER_URL=http://localhost:9200`
- `ELASTICSEARCH_APIKEY=local-dev-api-key` (for local dev - Elasticsearch security is disabled)

### Running the Application

For local development, you can use the `application-local.yml` profile:

```bash
# Using Gradle
./gradlew bootRun --args='--spring.profiles.active=local'

# Or set environment variables
export ELASTICSEARCH_APIKEY=local-dev-api-key
./gradlew bootRun
```

**Note**: The Elasticsearch container has security disabled for local development. The API key in `application-local.yml` is a placeholder and won't be validated. In production, you should enable Elasticsearch security and use a real API key.

## Data Persistence

All data is persisted in Docker volumes:
- `postgres_data` - PostgreSQL data
- `mongodb_data` - MongoDB data
- `elasticsearch_data` - Elasticsearch data

Data persists even after stopping containers. To remove all data:

```bash
docker-compose down -v
```

