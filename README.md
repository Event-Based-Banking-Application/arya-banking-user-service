# Arya Banking User Service

Core user domain microservice — multi-step registration, profile management, security tracking, and Keycloak identity sync.

## Quick Start

```powershell
# Prerequisites: Docker infra running, Vault unsealed, common library built
mvn clean spring-boot:run
```

The service starts on port **8086** and registers as `ARYA-BANKING-USER-SERVICE` in Eureka.

## Links

- [Local Development Setup](https://event-based-banking-application.github.io/arya-banking/docs/local-development/)
- [User Service Docs](https://event-based-banking-application.github.io/arya-banking/docs/user-service/)
