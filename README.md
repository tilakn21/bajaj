# Spring Boot Webhook Application

This application implements a Spring Boot service that:
1. Generates a webhook by sending a POST request on startup
2. Processes the webhook response
3. Submits a SQL query solution using JWT authentication

## Features
- Automatic webhook generation on startup
- JWT token handling
- SQL query submission
- No controllers/endpoints (Event-driven)

## Build & Run
```bash
mvn clean package
java -jar target/webhookapp-0.0.1-SNAPSHOT.jar
```

## Author
Tilak Neema