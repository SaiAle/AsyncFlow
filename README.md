# AsyncFlow — AI-Powered Async Task Management

[![CI/CD](https://github.com/SaiAle/AsyncFlow/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/SaiAle/AsyncFlow/actions)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://react.dev)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> A production-grade, multi-tenant task management platform powered by AI scheduling,
> Apache Kafka event sourcing, and Spring WebFlux reactive streams.

## Key Features

| Feature | Description |
|---|---|
| **AI Task Parsing** | Natural language input parsed by GPT-4o-mini via LangChain4j |
| **Smart Retry** | AI-predicted retry delays based on failure history |
| **Multi-Tenancy** | Full tenant isolation — per-tenant Kafka topics & DB rows |
| **Event Sourcing** | Immutable task event log with full replay capability |
| **Live Dashboard** | WebSocket-powered real-time updates, Recharts visualizations |
| **Circuit Breaker** | Resilience4j — AI service gracefully degrades |
| **Observability** | Prometheus metrics, OpenTelemetry tracing, Grafana-ready |

## Quick Start (Docker Compose)

```bash
git clone https://github.com/SaiAle/AsyncFlow.git
cd AsyncFlow
cp .env.example .env
echo "OPENAI_API_KEY=sk-your-key" >> .env
docker compose up --build
# Open http://localhost:3000
```

## Tech Stack

| Layer | Technology |
|---|---|
| **API** | Spring Boot 3.2, Spring WebFlux, Project Reactor |
| **AI** | LangChain4j 0.29, OpenAI GPT-4o-mini |
| **Messaging** | Apache Kafka 7.6, 6 partitions per topic |
| **Database** | PostgreSQL 16, R2DBC (reactive), Flyway migrations |
| **Cache** | Redis 7 (reactive) |
| **Resilience** | Resilience4j — Circuit Breaker, Retry, Rate Limiter |
| **Frontend** | React 18, TypeScript, Vite, Tailwind CSS, Recharts |
| **CI/CD** | GitHub Actions, Docker Buildx, GHCR |

## Author

**Sai Kumar Ale**
- GitHub: [@SaiAle](https://github.com/SaiAle)
- LinkedIn: [sai-kumar-a-1bb808284](https://linkedin.com/in/sai-kumar-a-1bb808284)
- Portfolio: [saiale.github.io](https://saiale.github.io)

*Part of a 3-project portfolio series — see also [FinPulse](https://github.com/SaiAle/FinPulse) and [VaultSecure](https://github.com/SaiAle/VaultSecure)*
