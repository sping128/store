# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the app (dev mode with hot reload via devtools)
./mvnw spring-boot:run

# Build
./mvnw package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=StoreApplicationTests

# Skip tests during build
./mvnw package -DskipTests
```

API docs (Swagger UI) are available at `http://localhost:8080/swagger-ui.html` when the app is running.

## Purpose

This is a Java Spring Boot learning project. The user asks Claude to teach Spring Boot concepts by writing working code directly in this repo. When teaching:
- Introduce one concept at a time with a minimal, concrete code example
- Explain the "why" behind Spring annotations and patterns — don't assume prior Spring knowledge
- Prefer adding to or extending existing code over creating isolated throwaway examples

## Architecture

This project follows the Code With Mosh Java Spring Boot course. The package structure is a standard layered architecture under `com.codewithmosh.store`:
