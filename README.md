# CDI Batch Job

Spring Batch job application that reads data from MongoDB, transforms it, and indexes it to Elasticsearch.

## Features

- **Spring Batch** job processing with chunk-based reading
- **MongoDB** as source database
- **PostgreSQL** as Spring Batch job repository
- **Elasticsearch** as target search engine
- **Automatic index initialization** and validation
- **Index alias management** for zero-downtime updates

## Prerequisites

- Java 21
- Gradle 8.x or higher
- Docker Desktop (for running services locally)

## Quick Start

### 1. Start Docker Services

```bash
# Start all services (PostgreSQL, MongoDB, Elasticsearch)
docker-compose up -d

# Check service status
docker-compose ps
```

### 2. Run the Application

```bash
# Build the project
./gradlew build

# Run with local profile (uses docker-compose services)
./gradlew bootRun --args='--spring.profiles.active=local'

# Or set environment variables
export ELASTICSEARCH_APIKEY=local-dev-api-key
./gradlew bootRun
```

## Configuration

### Docker Services

All services are configured in `docker-compose.yml`:

- **PostgreSQL**: `localhost:5432` (database: `batch_job_repository`)
- **MongoDB**: `localhost:27017` (database: `cdi_database`)
- **Elasticsearch**: `localhost:9200` (security disabled for local dev)

### Application Configuration

Main configuration is in `src/main/resources/application.yml`

For local development, use `application-local.yml` profile which includes:
- Pre-configured connection strings
- Placeholder API key for Elasticsearch (security disabled)

### Environment Variables

Key environment variables:

```bash
# Database
DB_USERNAME=postgres
DB_PASSWORD=postgres

# MongoDB
MONGODB_URI=mongodb://localhost:27017/cdi_database

# Elasticsearch
ELASTICSEARCH_SERVER_URL=http://localhost:9200
ELASTICSEARCH_APIKEY=your-api-key
ELASTICSEARCH_INDEX_NAME=cdi_content
ELASTICSEARCH_ALIAS=cdi_content_alias

# Batch Job
BATCH_JOB_CHUNK_SIZE=1000
```

## Job Flow

The batch job executes the following steps:

1. **Index Initialization** - Creates Elasticsearch index if it doesn't exist
2. **Data Sync** - Reads from MongoDB, transforms, and indexes to Elasticsearch
3. **Validation** - Verifies all records were transferred successfully
4. **Index Flip** - Updates Elasticsearch alias to point to new index

## Project Structure

```
src/main/java/com/patientpoint/cdi/
├── batch/
│   ├── config/          # Batch job configuration
│   ├── listener/        # Job execution listeners
│   ├── processor/       # Data transformation logic
│   ├── reader/          # MongoDB data reader
│   ├── runner/          # Job execution runner
│   ├── service/         # Business services
│   ├── tasklet/         # Tasklet-based steps
│   └── writer/          # Elasticsearch writer
├── config/              # Spring configuration
├── model/               # Data models
└── repository/          # MongoDB repositories
```

## Testing

Run unit tests:

```bash
./gradlew test
```

## Docker Services Management

See `docker/README.md` for detailed Docker setup instructions.

## License

[Your License Here]

