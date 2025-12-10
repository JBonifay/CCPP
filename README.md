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

- **Backend**: Java 25 + Spring Boot 4.0.0
- **Event Store**: In-memory (later EventStoreDB)
- **Database**: In-memory (later PostgreSQL)
- **Testing**: JUnit 5 + AssertJ
- **Blog**: Astro

## 🚀 Getting Started

### Prerequisites

- Java 25
- Maven 3.9+
- Node.js 20+ (for blog)

### Build the Project

```bash
# Build all modules
mvn clean install

# Run tests
mvn test
```

## 📖 Project Structure

```
ccpp/
├── .github/workflows/     # CI/CD workflows
├── shared/                # Shared kernel (value objects, base classes)
├── ApiGateway/            # Authentication and routing
├── ProjectPlanning/       # Core domain - project planning
├── TeamCollaboration/     # Workspace context
├── ContentPlanning/       # Ideation context
├── Notification/          # Supporting domain
├── BudgetManagement/      # (Optional) Separate budget context
├── blog/                  # Technical blog (Astro)
├── pom.xml                # Parent POM
├── PLAN.md                # Architecture plan
└── INITIAL_CONVERSATION.md # Architecture discussion
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

