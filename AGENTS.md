# Repository Guidelines

## Project Direction

This repository is the Spring Boot Modulith REST API for Springs of MUD. The base package is `org.springy.som.modulith`. Java 17, Spring Boot 3.5.9-SNAPSHOT, Spring Modulith 1.4.14. The app listens on port 9080. The MongoDB database is `SOMDB`.

World content and the agent documents live here. `springs-of-mud-server` loads content from this API. `springs-of-mud-designer` authors content through it. `springs-of-mud-agentic-player` reads agents, prompts, memory, and knowledge from it. `springs-of-mud-ui` does not author world content.

Do not update any other repository to meet the requirements of the current iteration. That includes springs-of-mud-agentic-player, springs-of-mud-server, springs-of-mud-ui, springs-of-mud-designer, and every other checkout. Implement the iteration in this modulith. Read those repositories when you need a contract. Leave their source, tests, config, and builds unchanged unless the user explicitly asks for a change in that repository. A missing client, or a client that drops a field, is a gap to report. It is not permission to edit that client in this iteration.

## Reference

Open a reference when the work touches that area.

- Module map, named interfaces, and the agent resource routes ([Architecture](docs/references/Architecture.md)).
- Which skill to follow, and what this repository already selected ([Skills](docs/references/Skills.md)).
- Maven wrapper commands and `ModulithVerificationTest` ([Build and Test](docs/references/BuildAndTest.md)).
- Port, database, security, memory retention, and document templates ([Configuration](docs/references/Configuration.md)).

## Coding Conventions

Module structure and the Modulith feature table belong to the spring-modulith skill. Java review findings belong to the java-code-reviewer skill. The conventions below are specific to this repository.

- Match the indentation of the file you edit. New files use 4 spaces. `SomServerApplication` and `ModulithVerificationTest` use tabs. Leave those tabs.
- Use explicit imports. A `package-info.java` imports the annotation and uses its simple name, as `domain` does.
- Put types another module may use in `api`: the `*Api` interface, the `*View` record, and the `*Mapper`. Annotate that package `@NamedInterface("api")`. `*Api` methods take and return the document type, as `AreaApi` does. `*Mapper` is a final class with a private constructor and a static `toView`. The Mongo document is a Lombok `@Data` class in `internal`. Keep the existing `@Document` collection name.
- A controller stays in `internal`. It takes the concrete service in its constructor, accepts the document on a write, and returns the mapper view. JSON field names stay the field names of the stored document.
- A module-specific failure extends `NotFoundApiException`, `InvalidRequestApiException`, `ConflictApiException`, or `ServiceUnavailableApiException`, and carries `@ApiError`. The handler is a `@RestControllerAdvice(assignableTypes = …Controller.class)` that extends `BaseApiExceptionHandler`. Responses are `ProblemDetail`.
- Wrap a `DataAccessException` in the module persistence exception. Log the cause. The message returned to the caller stays stable and does not include the raw exception text.
- Use `java.time`. A required duration fails startup when it is missing, zero, or negative. `som.memory.retention` has no Java default.
- A passage of time uses a Moments type such as `HourHasPassed`. Listen with `org.springframework.modulith.events.ApplicationModuleListener`. Leave `spring.modulith.moments.granularity` at hours. Leave `spring.modulith.moments.enable-time-machine` unset.
- Keep `domain` as one closed module. Leave its capabilities as nested packages.
- Tests are package-private JUnit 5. A behavior that can fail has a unit test that asserts the outcome. After a boundary change, run `ModulithVerificationTest`, which calls `ApplicationModules.of(SomServerApplication.class).verify()`. On PowerShell, quote a `-Dtest` property: `.\mvnw.cmd "-Dtest=ModulithVerificationTest" test`.
- `@DataMongoTest` loads `SomServerApplication`, so `@EnableAsync` runs listeners off the test thread. A slice that must see those writes inside the test transaction imports a test-only `SyncTaskExecutor` bean named `taskExecutor`. Production listeners stay asynchronous.
- `@ApplicationModuleTest` for `knowledge`, `memory`, `prompt`, and `agents` is still outstanding. Add it when the user asks for module tests. Until then, unit tests and `ModulithVerificationTest` cover those modules.
- Leave versions off artifacts the Spring Boot parent or `spring-modulith-bom` manages.
- Comment a constraint the next reader cannot see from the code.
- Do not copy passwords, client secrets, keystore material, JWTs, or `ENC(...)` values into source, tests, reports, or this file.

## Change Discipline

- Do not update another repository to meet the requirements of the current iteration. Implement the change in this modulith. Leave springs-of-mud-agentic-player, springs-of-mud-server, springs-of-mud-ui, springs-of-mud-designer, and every other checkout unchanged unless the user explicitly asks for that repository to change.
- Keep published `/api/v1` paths, status codes, and JSON field names compatible with the clients that already call them.
- Keep world content in the closed `domain` module and agent documents in `knowledge`, `memory`, `prompt`, and `agents`. The MongoDB database name stays `SOMDB`.
- Leave security, `src/main/resources/templates`, and Moments granularity unchanged unless the user asks for that change.
- Do not commit passwords, client secrets, keystore material, JWTs, or `ENC(...)` values.
- Avoid a broad refactor of an existing domain capability unless a test covers the change.
