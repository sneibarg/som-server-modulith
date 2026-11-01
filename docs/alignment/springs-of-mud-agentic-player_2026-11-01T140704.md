# Alignment report

**Target:** `springs-of-mud-agentic-player` at `C:\Users\scott\PycharmProjects\Auto-GPT\springs-of-mud-agentic-player`
**Reference:** `som-server-modulith` at `E:\IdeaProjects\som-server-modulith`
**Scope:** Working trees as read on 2026-11-01. No commit range was given. `AGENTS.override.md` is not present in either repository. The target's `AGENTS.md` was read. This report proposes changes only.

The target persists `WorldKnowledge` and `WorkingMemory` through REST. Every HTTP client under the target was read, including `src/`, `tests/`, `resources/`, `AGENTS.md`, and `README.md`.

## REST clients

| Client | Call | Where it is aimed | Caller |
| --- | --- | --- | --- |
| `AuthClient` | `POST /api/auth/login` | Auth origin, default `http://localhost:8169` | `AccountSession.connect` |
| `KnowledgeClient` | `GET` and `PUT /api/v1/agent-knowledge/{characterId}` | `resources/agent.yml` `knowledge.base_url`, currently `http://localhost:8169` | `src/main.py` `_save_knowledge` calls `save` after `play`. `load` is only used from `tests/test_knowledge_client.py`. |
| `OllamaClient` | `POST /ollama/completions` | `http://localhost:9081` | `Bootstrapper.learn_categories`, `learn_commands`, and `learn_help`, and `CommandCategorizer` |

`AuthClient` and `OllamaClient` do not call this application. This application publishes neither `/api/auth/login` nor `/ollama/completions`. No change is proposed for those two clients.

`WorkingMemory` has no REST client. `PlayerControl._dump_memory` writes `to_review()` to a local file. `write_collection` writes `to_collection()` into `resources/collections/SOMDB.Memories.json`. Production code does not call `write_collection`. `play` saves world knowledge and does not save working memory.

## This application

Spring Boot `3.5.9-SNAPSHOT`, Java 17. `spring-modulith.version` is `1.3.0-M3`. The BOM is already imported. Artifacts already declared: `spring-modulith-starter-core`, `spring-modulith-starter-mongodb`, `spring-modulith-starter-test`, `spring-modulith-events-kafka`, `spring-modulith-actuator`.

`SomServerApplication` is the only type in `org.springy.som.modulith`. Modulith therefore sees these modules:

- `config`
- `domain`, closed by `@ApplicationModule(type = CLOSED)`. Its capability packages stay inside that one module.
- `exception`
- `web`

No controller mapping mentions `agent-knowledge`, `agent-memory`, `WorldKnowledge`, or `WorkingMemory`.

Boot 3.5 is compiled against Modulith 1.4. The newest stable 1.4 patch on Maven Central at the time of this report is `1.4.14`.

## Module map for the two REST resources

Add two capability modules as direct subpackages of `org.springy.som.modulith`. Do not put them under `domain`. `domain` already groups unrelated capabilities, and these two documents are not part of that content model.

| Module | Allowed dependencies | HTTP |
| --- | --- | --- |
| `knowledge` | `exception` | `GET` and `PUT /api/v1/agent-knowledge/{characterId}` |
| `memory` | `exception` | `GET` and `PUT /api/v1/agent-memory/{agentName}` |

Neither module references the other. Neither references `domain`, `config`, or `web`. Controllers, documents, and services stay in `internal`. No `@NamedInterface`. The sweep event is `org.springframework.modulith.moments.HourHasPassed`, a library type, so the memory allow-list stays `exception`.

`package-info.java` on each module base package:

```java
@org.springframework.modulith.ApplicationModule(allowedDependencies = "exception")
package org.springy.som.modulith.knowledge;
```

The memory module uses the same annotation. Its expiration listener stays inside the module, so the allow-list does not grow.

Each module gets a controller, a document, a repository, a service, a not-found exception, an invalid-request exception, and a `@RestControllerAdvice` that extends `org.springy.som.modulith.exception.BaseApiExceptionHandler` and is assigned to that controller. That import is why `exception` is the allow-list entry. Mongo collections: `AgentKnowledge` and `Memories`. `Memories` is the collection name already used by `SOMDB.Memories.json`. The database stays the one named in `application.yml`.

### Working memory expiration

`som.memory.retention` is a `java.time.Duration` bound on a `@ConfigurationProperties` type in `org.springy.som.modulith.memory.internal`. It is how long a stored working-memory document is kept. The value is not chosen here. A missing, zero, or negative duration fails startup. This is the one new `application.yml` entry. The templates stay unchanged.

`PUT` sets `savedAt` to the current `Instant` and ignores a `savedAt` sent by the client. `GET` returns 404 when `savedAt` is older than the retention, and when `savedAt` is absent. The target does not send `savedAt`. `MemoryClient.save` may receive it on the response and does not need to store it.

`spring-modulith-moments` publishes the sweep. `org.springframework.modulith.moments.support.Moments` already runs `@Scheduled(cron = "@hourly")` and publishes `HourHasPassed`. `SomServerApplication` already has `@EnableScheduling`. Add `@EnableAsync` so the listener runs. Do not add a thread, a `TaskScheduler`, or a module event that stands in for that hour. Leave `spring.modulith.moments.granularity` at its default, `hours`. Do not set `spring.modulith.moments.enable-time-machine` in `application.yml`.

`MemoryExpirationListener` in `memory.internal` uses `org.springframework.modulith.events.ApplicationModuleListener` on `HourHasPassed`. `getTime()` is the hour that just passed, as a `LocalDateTime` truncated to the hour in UTC. The cutoff is that time plus one hour, minus `som.memory.retention`. Delete every `Memories` document whose `savedAt` is absent or strictly before that cutoff. A replay of the same event keeps the same cutoff. The listener does not inject `Moments`. The MongoDB event publication registry, already on the classpath through `spring-modulith-starter-mongodb`, retries the listener when it fails or does not finish. Add no second registry starter and do not mark the event `@Externalized`.

`HourHasPassed` is the finest moment, so a document can remain up to almost one hour past the configured retention. A shorter sweep is outside Moments. World knowledge is not part of this listener.

`PUT` inserts or replaces the one document for that key and returns the stored JSON. `GET` returns that JSON, or `404` when none exists. `KnowledgeClient.load` treats only HTTP 404 as "nothing stored." Any other status at or above 400 fails the call. Do not add list, create, or delete routes. The target never calls them.

Return the stored document itself. A view with different field names would fail `KnowledgeClient.save`, which accepts the response only when it is a JSON object, and `WorldKnowledge.from_document`.

### Features to add

- `ApplicationModules.verify()` in the existing `ModulithVerificationTest`.
- `@ApplicationModule(allowedDependencies = "exception")` on `knowledge` and `memory`.
- `@EnableAsync` on `SomServerApplication`.
- `spring-modulith-moments`, with its version taken from the BOM. `@ApplicationModuleListener` on `MemoryExpirationListener` for `HourHasPassed`. The MongoDB event publication registry is already provided by `spring-modulith-starter-mongodb`.
- `@ApplicationModuleTest` for `knowledge`, `STANDALONE`, asserting the GET miss and the PUT-then-GET round trip. That module publishes no event.
- `@ApplicationModuleTest` for `memory`, `STANDALONE`. Publish `HourHasPassed.of(...)` with `Scenario`, wait for the collection change, and verify that a document inside the retention remains and a document past the cutoff is gone. Clean the fixture documents with `andCleanup`. The stimulus transaction is not rolled back.
- `@WebMvcTest` on each new controller, matching the controller tests already in this repository.

### Features to leave out

- Promoting the existing `domain` capabilities to direct subpackages. The target does not call those contracts.
- `@NamedInterface`. Nothing outside the new modules names their types.
- `@Externalized` and any new broker artifact. `HourHasPassed` stays inside this process. Leave the existing `spring-modulith-events-kafka` dependency as it is.
- A second event publication registry. `spring-modulith-starter-mongodb` already supplies the one this listener needs.
- A memory-module thread, scheduler, or `MemoryExpirationDue` event. Moments already publishes the hour.
- `spring.modulith.moments.enable-time-machine`, and a change to `spring.modulith.moments.granularity`. The module test publishes `HourHasPassed` directly.
- `ApplicationModuleInitializer`, `spring-modulith-starter-insight`, `Documenter`, `Type.OPEN`, nested `@ApplicationModule`, and `spring-modulith-junit`.
- `@ApplicationModuleTest` for the modules that already exist. Their controller slices stay `@WebMvcTest`.
- Changes to `src/main/resources/templates`, and any `application.yml` entry other than `som.memory.retention`. Security stays as configured. `som.web-security.enabled` is false, so the current process does not check the `Authorization` header these clients send. Whether a token from `POST /api/auth/login` carries `ROLE_somplayer`, `ROLE_game-master`, or `ROLE_somadmin` when security is turned on is still unknown.

## Findings

### 1. World knowledge has no published contract

- **Where:** `src/net/rest/KnowledgeClient.py` in the target. No matching controller in this repository.
- **Standard:** `KnowledgeClient` and the target's `AGENTS.md` World Knowledge API. `GET {base}{path}/{characterId}` returns the document or 404. `PUT` replaces the whole document and returns it. `characterId` in the body matches the path.
- **Proposed change:** In this repository, add `org.springy.som.modulith.knowledge` as described above. The document fields are the camelCase object from `WorldKnowledge.to_document`: `characterId`, `characterName`, `agentName`, `loginLines`, `helpTopics`, `areas`, `mobiles`, `npcs`, `players`, and `room`. `room` may be null. Nested `helpTopics[].topics` keep `keyword` and `text`. Store the arrays in the order received.

### 2. Working memory is not saved through REST

- **Where:** `src/memory/WorkingMemory.py`, `src/control/PlayerControl.py`, `src/main.py`. No memory client under `src/net/rest`.
- **Standard:** The stated contract is that `WorkingMemory` is persisted via REST. The document the class already builds for storage is `to_collection(agent_name)`, not the snake_case `to_review()` file.
- **Proposed change:** In the target, add `src/net/rest/MemoryClient.py` with the same `save` and `load` behavior as `KnowledgeClient`, default path `/api/v1/agent-memory`. Identity is `agentName`, matched ignoring case, which is the rule in `write_collection`. Export it from `src/net/rest/__init__.py`. After `memory.absorb_world(knowledge)` in `src/main.py`, save `memory.to_collection(agent.name)` the same way `_save_knowledge` saves the knowledge document. A failed save is reported and the session continues. Leave `PlayerControl._dump_memory` writing the local review file. In this repository, add the `memory` module. The body fields are `agentName`, `players`, `agenticPlayers`, `mobiles`, `npcs`, `aggressiveMobiles`, `rooms`, `commands`, `considers`, and `conversations`. The server adds `savedAt` on `PUT`. The target omits it. Expiration of that field is finding 7. Being fields are `name`, `kind`, `agentic`, `roomName`, `aggressive`, and `auras`. Room fields are `name`, `description`, and `exits`. Command fields are `name`, `category`, and `helpText`. Consider fields are `target`, `subject`, `kind`, `outlook`, `verdict`, and `text`. Conversation fields are `topic`, `location`, `locationName`, `participants`, and `interest`.

### 3. Knowledge is pointed at the auth origin

- **Where:** `resources/agent.yml` `knowledge.base_url`, and the default in `src/agent/AgentConfig.py` when that value is omitted. `tests/test_agent_collection.py` expects `http://localhost:8169`.
- **Standard:** `KnowledgeClient` must reach the server that implements `/api/v1/agent-knowledge`. This application listens on port 9080. The auth origin on port 8169 is `AuthClient`'s server.
- **Proposed change:** In the target, set `knowledge.base_url` to this application's origin and update the assertion in `tests/test_agent_collection.py`. Leave `auth.base_url` on port 8169. No password, token, or header value belongs in that file.

### 4. A world-knowledge round trip drops auras

- **Where:** `src/play/WorldKnowledge.py` `to_document`, `_being_document`, `_player_document`, `_being_from`, and `_player_from`. `src/play/WorldReader.py` stores `auras` on `PlayerRecord` and `BeingRecord`.
- **Standard:** The persisted class is `WorldKnowledge`. `AGENTS.md` lists the same body `to_document` writes today and also omits `auras`, so the written contract and the method agree. The object still holds aura labels that `GET` then `from_document` cannot restore.
- **Proposed change:** In the target, add `auras` to the player, mobile, and npc objects in both directions. In this repository, accept and return that array on those three nested objects. Omit it nowhere else. `WorkingMemory` already keeps `auras` on its beings.

### 5. Modulith generation does not match Boot 3.5

- **Where:** `pom.xml` property `spring-modulith.version`.
- **Standard:** `.grok/skills/spring-modulith/SKILL.md` Dependencies. Boot 3.5 uses Modulith 1.4. Import the newest stable patch. The project already uses a milestone (`1.3.0-M3`), which is the Boot 3.4 generation.
- **Proposed change:** In this repository, set `spring-modulith.version` to `1.4.14`. Leave the BOM import and the individual artifacts in place. Add no further Modulith starter. `starter-mongodb` and `starter-test` already cover these modules. `spring-modulith-moments` is finding 7 and takes its version from this BOM.

### 6. The structural test does not call `verify()`

- **Where:** `src/test/java/org/springy/som/modulith/ModulithVerificationTest.java`.
- **Standard:** `.grok/skills/spring-modulith/SKILL.md` Enforce boundaries. `ApplicationModules.of(SomServerApplication.class).verify()` is the check. The sample command in that skill names `ModularityTests`. This repository's test is `ModulithVerificationTest`, and it calls `detectViolations().throwIfPresent()`.
- **Proposed change:** In this repository, change that test to call `verify()`. Keep the class name. Run `.\mvnw.cmd -Dtest=ModulithVerificationTest test`. Do not set `verifyAutomatically` to false, do not exclude a package, and do not mark a module `Type.OPEN`.

### 7. Stored working memory does not expire

- **Where:** No retention property or `savedAt` field exists. The `Memories` collection is part of the proposed `memory` module. `pom.xml` does not declare `spring-modulith-moments`.
- **Standard:** Working memory is retained for `som.memory.retention`. `.grok/skills/spring-modulith/SKILL.md` uses Moments for a passage of time. `HourHasPassed` is the hourly event. The listener removes every document in the collection older than the configured duration. The MongoDB registry retries that listener.
- **Proposed change:** In this repository, add `spring-modulith-moments`, `som.memory.retention`, `savedAt`, and `MemoryExpirationListener` as described under Working memory expiration. Do not add a sweep thread. World knowledge is not deleted by this listener.

## Summary

Seven proposed changes. Two new modules in this repository, `knowledge` and `memory`. The memory module keeps each document for `som.memory.retention` and deletes older ones when Moments publishes `HourHasPassed`. Also the Modulith version and `verify()` update. In the target, a `MemoryClient` and a save on the play path, a knowledge base URL that reaches this application, and `auras` on the world-knowledge document. `AuthClient` and `OllamaClient` need nothing from this application. A document can remain for almost an hour after its retention, because that is the finest Moments event.
