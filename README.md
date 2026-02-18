# Event Notification Service

## Overview
Event Notification Service is a business-oriented distributed application that simplifies event organization and internal communication. Administrators create events via a web client; employees connect using a Telegram bot (by username) and receive automatic notifications about events targeted to them.

## System architecture
The system consists of three cooperating services:

- **Backend Service 1** — authentication, event creation and storage (PostgreSQL), publishes event messages to Kafka.
- **Backend Service 2** — hosts the Telegram bot, subscribes to Kafka topics, filters events by user, and sends Telegram notifications.
- **Frontend Service** — React web client for administrators to create and manage events and send event topics to the backend.

Message flow:
1. Admin creates an event in the frontend.
2. Frontend calls Backend Service 1 (authenticated).
3. Backend Service 1 persists the event and publishes a message to Kafka (topic: `events`).
4. Backend Service 2 consumes `events`, matches recipients, and sends messages via the Telegram Bot API.

## Technologies
- Java & Spring Boot
- Spring Security
- React (JavaScript)
- PostgreSQL
- Apache Kafka
- Docker \& Docker Compose
- Telegram Bot API
- Maven (build)

## Requirements
- Docker and Docker Compose (recommended)
- Java 17+
- Maven
- Node.js + npm (for frontend development)
- Kafka (can be run via `docker-compose`)
- PostgreSQL (can be run via `docker-compose`)

## Configuration (important environment variables)
Common variables (examples):
- `POSTGRES_DB` — database name
- `POSTGRES_USER` — DB user
- `POSTGRES_PASSWORD` — DB password
- `KAFKA_BOOTSTRAP_SERVERS` — Kafka broker addresses
- `TELEGRAM_BOT_TOKEN` — Telegram bot token (Backend Service 2)
- `JWT_SECRET` — authentication secret (Backend Service 1)
- `SERVER_PORT` — service port overrides

Place service-specific settings in `application.properties` / `application.yml` for each backend service and supply secrets via environment variables or a secret manager.

## Quick start (docker-compose)
A single `docker-compose.yml` can orchestrate PostgreSQL, Zookeeper, Kafka, both backends and the frontend for local testing.

Example:
1. Fill `.env` with required variables (DB, Kafka, Telegram token).
2. Start:
docker compose up --build
3. Visit frontend at `http://localhost:3000` (or configured port). Backend APIs default to ports set in service configs.

## Local development
Backend (each service):
cd backend-service-1 mvn clean package mvn spring-boot:run

Frontend:
cd frontend npm install npm start

## Kafka & topics
- Topic used: `events`
- Producer: Backend Service 1 (publishes event payloads)
- Consumer: Backend Service 2 (filters and routes notifications)

Ensure `KAFKA_BOOTSTRAP_SERVERS` points to your Kafka cluster.

## Telegram Bot setup
1. Create a bot with BotFather and obtain `TELEGRAM_BOT_TOKEN`.
2. Employees must share their Telegram username with the system (web UI or registration flow).
3. Backend Service 2 maps usernames to Telegram chat IDs (store mapping on first interaction or via explicit registration).
4. Backend Service 2 sends notifications using the Bot API.

## Security
- Use HTTPS & secure networking for production.
- Keep `JWT_SECRET`, `TELEGRAM_BOT_TOKEN` and DB passwords out of source control.
- Use role-based access controls via Spring Security for admin endpoints.

## API (high level)
- `POST /api/auth/login` — authenticate admin users (returns JWT)
- `POST /api/events` — create event (admin, authenticated)
- `GET /api/events` — list events (admin)
- Websocket / push endpoints or Kafka for internal notifications between services

Adjust endpoints according to the actual implementation.

## Monitoring & logs
- Collect logs from each service (Docker / ELK / Tempo).
- Monitor Kafka consumer lag and DB health.
- Configure Spring Boot actuator endpoints for health checks.

