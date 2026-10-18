# Event Notification Service

Administrators create events in a web client; employees subscribe through a Telegram bot and receive a
notification for every event addressed to them.

## Architecture

| Folder | Role | Stack |
| --- | --- | --- |
| `api-service` | Authentication (JWT), events, users; publishes events to Kafka | Spring Boot, Spring Security, JPA, Flyway, PostgreSQL |
| `bot-consumer` | Consumes Kafka, sends Telegram messages, stores subscriptions | Spring Boot, Kafka, Telegram Bots API, PostgreSQL |
| `client-service` | Web UI (login, create / list / delete events) | React, Vite, nginx |

Message flow:

1. An admin creates an event in the UI → `POST /api/events`.
2. `api-service` stores it with `sent = false` (transactional outbox).
3. `EventOutboxPublisher` publishes unsent rows to the Kafka topic `events` (key = recipient username),
   waits for the broker acknowledgement and then marks the row `sent = true`. Nothing is lost or re-sent after a restart.
4. `bot-consumer` reads the topic and sends a Telegram message if the recipient has subscribed.
   Messages that cannot be processed go to the dead-letter topic `events.DLT`.

## Quick start

```bash
cp .env.example .env        # fill in the secrets, see below
docker compose up --build
```

* UI: http://localhost:3002
* Swagger UI: http://localhost:8090/swagger-ui.html
* Optional tools (Kafka UI on :8080, pgAdmin on :5050, bound to localhost): `docker compose --profile tools up`

### Configuration (`.env`)

| Variable | Used by | Meaning |
| --- | --- | --- |
| `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` | postgres, both backends | database credentials |
| `JWT_SECRET` | api-service | base64 key, ≥ 256 bit: `openssl rand -base64 48` (required) |
| `ADMIN_USERNAME`, `ADMIN_PASSWORD` | api-service | first admin, created on startup if missing |
| `TELEGRAM_BOT_TOKEN`, `BOT_USERNAME` | bot-consumer | bot created with [@BotFather](https://t.me/BotFather) |
| `CORS_ALLOWED_ORIGINS` | api-service | comma separated origins (not needed behind the nginx proxy) |
| `KAFKA_SERVERS` | both backends | defaults to `kafka2:19092` |

The database schema is created by Flyway migrations in `api-service/src/main/resources/db/migration`.
Nothing but the web port (3002) and the API port (8090) is exposed to the network.

## Users and roles

* Self-registration (`POST /auth/register`) always creates a `USER`. Roles can **not** be chosen by the client.
* The first `ADMIN` is created from `ADMIN_USERNAME` / `ADMIN_PASSWORD`.
* Usernames are stored lower-case and must equal the user's **Telegram username** – that is how notifications are routed.

## Telegram bot

1. Create a bot with BotFather, put the token into `.env`.
2. Register in the web app with your Telegram username as the username.
3. Open the bot and send `/start`. The bot identifies you by the username of your Telegram account
   (nobody can subscribe with somebody else's name). `/stop` unsubscribes.
   Subscriptions are stored in PostgreSQL and survive restarts.

## API

All endpoints except `/auth/**`, Swagger and `/actuator/health` need `Authorization: Bearer <token>`.

| Method & path | Who | Description |
| --- | --- | --- |
| `POST /auth/register` | everyone | create a `USER`, returns `{token, username, role}` |
| `POST /auth/login` | everyone | returns `{token, username, role}` |
| `GET /events` | user / admin | own events (admins: all events) |
| `POST /events` | admin | create an event for `userId` |
| `DELETE /events/{id}` | owner / admin | delete an event |
| `GET /users` | admin | list users (to pick a recipient) |

Errors are JSON: `{"error": "..."}` with 400 / 401 / 403 / 404 / 409.

## Local development

* Easiest: `docker compose up --build` (the Kafka listeners are only advertised inside the compose network).
* Frontend with hot reload: `cd client-service && npm install && npm run dev` (proxies `/api` to `localhost:8090`).
* Backends need **JDK 21** (JDK 25+ currently breaks Lombok): `./mvnw spring-boot:run` inside the service folder,
  with `JWT_SECRET`, `DB_URL`, `DB_PASSWORD` and `KAFKA_SERVERS` set.

## Tests & CI

```bash
(cd api-service && ./mvnw test)
(cd bot-consumer && ./mvnw test)
(cd client-service && npm test)
```

`.github/workflows/build.yml` runs all tests on every push / pull request. On `main` it also builds the
changed images, pushes them to `ghcr.io/<owner>/<service>` and deploys over SSH (`docker compose pull && up -d`).
The server needs a `.env` next to `docker-compose.yml` and, for private packages, a `docker login ghcr.io`.

## Monitoring

Both backends expose `GET /actuator/health` (used by the compose health checks).
