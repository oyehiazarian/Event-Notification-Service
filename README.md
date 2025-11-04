Event Notification Service

Ein Business-Tool zur einfachen Organisation von Events.
Im Web-Client kann man Events für bestimmte Mitarbeiter erstellen.
Die Mitarbeiter verbinden sich über einen Telegram-Bot mit ihrem Benutzernamen – und erhalten automatisch Benachrichtigungen über neue Events direkt im Messenger.


Systemarchitektur – Event Notification Service

Die Anwendung besteht aus mehreren Services und Tools:
Backend 1 – verantwortlich für Authentifizierung, Erstellung und Speicherung von Events sowie deren Publikation über Kafka.
Backend 2 – betreibt den Telegram-Bot, der Events abonniert, filtert und an die richtigen Benutzer sendet.
Frontend-Service – bietet eine Weboberfläche zum Erstellen von Events und Versenden von Topics.




Verwendete Technologien
Unsere Lösung basiert auf modernen, zuverlässigen Tools und Frameworks:

Java & Spring Boot – Kern der Backend-Logik
Spring Security – Authentifizierung und Zugriffsschutz
React (JavaScript) – Benutzerfreundliches Web-Frontend
PostgreSQL / pgAdmin4 – Datenverwaltung und Administration
Kafka – Event-Streaming und Kommunikation zwischen Services
Docker Compose – Containerisierung und einfaches Deployment