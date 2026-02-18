Event Notification Service is a business-oriented application designed to simplify event organization and internal communication.
Using the web client, administrators can create events for specific employees.
Employees connect to the system via a Telegram bot using their username and automatically receive notifications about new events directly in the messenger.


System Architecture
The application is built as a distributed system consisting of multiple services:

Backend Service 1:
 (Handles authentication,
 Creates and stores events,
 Publishes event data to Kafka)

Backend Service 2:
 (Hosts the Telegram Bot,
 Subscribes to Kafka events,
 Filters events by user,
 Sends notifications to the appropriate employees via Telegram)

Frontend Service:
 (Provides a web interface,
 Allows creation and management of events,
 Sends event topics to the backend)


Technologies Used
The solution is based on modern and reliable technologies:

(Java & Spring Boot – Core backend logic)
(Spring Security – Authentication and access control)
(React (JavaScript) – User-friendly web frontend)
(PostgreSQL – Persistent data storage)
(pgAdmin 4 – Database administration)
(Apache Kafka – Event streaming and inter-service communication)
(Docker Compose – Containerization and simplified deployment)
