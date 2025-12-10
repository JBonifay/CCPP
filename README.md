# CCPP - Content Creator Planning Platform

A multi-tenant content creator planning application demonstrating

- **Clean Architecture**
- **Domain-Driven Design (DDD)**
- **Event Sourcing**
- **CQRS**
- **Event-Driven Architecture**
- **Saga Choreography**
- **Test-Driven Development (TDD)**

## 📚 Documentation

- [**PLAN.md**](PLAN.md) - Complete architectural plan and implementation guide
- [**INITIAL_CONVERSATION.md**](INITIAL_CONVERSATION.md) - Architecture discussion and decision-making process
- [**Blog**](https://jbonifay.github.io/CCPP/) - Technical blog documenting the journey

## 🏗️ Architecture

### Bounded Contexts

1. **Workspace Context** - Multi-tenancy and subscription management
2. **Ideation Context** - Brainstorming and idea management
3. **Project Planning Context** ⭐ (Core Domain) - Video project planning with budget tracking
4. **Notification Context** - Email/SMS notifications

### Technology Stack

- **Frontend**: Angular 21 + TypeScript
- **Backend**: Java 25 + Spring Boot 4.0.0
- **API Gateway**: Spring Cloud Gateway
- **Event Bus**: Apache Kafka
- **Event Store**: In-memory (later EventStoreDB)
- **Database**: In-memory (later PostgreSQL)
- **Testing**: JUnit 5 + AssertJ (Backend), Vitest (Frontend)
- **Deployment**: Docker + Nginx + Traefik
- **Blog**: Astro

## 🚀 Getting Started

### Prerequisites

- Java 25
- Maven 3.9+
- Node.js 20+
- Docker (for deployment)

### Build the Project

**Backend:**
```bash
# Build all modules
mvn clean install

# Run tests
mvn test
```

**Frontend:**
```bash
cd frontend

# Install dependencies
npm install

# Run development server (with proxy to API Gateway on :8761)
npm start

# Build for production
npm run build

# Run tests
npm test
```

## 📖 Project Structure

```
ccpp/
├── .github/workflows/     # CI/CD workflows
├── frontend/              # Angular web application
├── shared/                # Shared kernel (value objects, base classes)
├── api-gateway/           # API Gateway with Spring Cloud Gateway
├── project-planning/      # Core domain - project planning
├── workspace/             # Workspace context (multi-tenancy)
├── Ideation/              # Ideation context
├── notification/          # Supporting domain
├── blog/                  # Technical blog (Astro)
├── compose.yaml           # Docker Compose for deployment
├── pom.xml                # Parent POM
└── PLAN.md                # Architecture plan
```

## 🎯 Learning Goals

This project demonstrates:

- ✅ **Clean Architecture** with dependency inversion
- ✅ **Domain-Driven Design** with bounded contexts
- ✅ **Event Sourcing** for complete audit trail
- ✅ **CQRS** with separate read/write models
- ✅ **Saga Choreography** for cross-context workflows
- ✅ **TDD** with high test coverage
- ✅ **Multi-tenancy** at domain level

## 📊 Current Status

**Phase**: Foundation + Planning
**Next Step**: Implement Project aggregate with TDD

See [PLAN.md](PLAN.md) for detailed roadmap.

## 🚢 Deployment

The application uses Docker Compose with Traefik for routing and Let's Encrypt for SSL.

**Architecture:**
- Frontend (Nginx) serves Angular app on `/`
- API Gateway handles all `/api/*` requests
- Kafka for event-driven communication between microservices
- Traefik routes traffic with automatic HTTPS

**Deploy:**
```bash
# Pull latest images and restart services
docker-compose pull
docker-compose up -d
```

**CI/CD:**
- Backend services are built and published to GHCR on push to main
- Frontend is built and published to GHCR on push to main
- GitHub Actions workflows handle automated builds and tests

---

## 📄 License & Usage

**Copyright © 2025 Joffrey Bonifay. All Rights Reserved.**

This code is publicly available for **educational and reference purposes only**.

### ✅ You MAY:

- View and study the code
- Use it as a learning resource
- Reference architectural patterns in your own learning

### ❌ You MAY NOT:

- Use this code in commercial projects
- Copy or redistribute this code
- Create derivative works
- Use this code in production systems

**This is a portfolio/learning project.** If you're interested in collaboration or have questions, feel free to reach
out via GitHub issues.

---

