# som-server-modulith

Spring Boot Modulith REST API for Springs of MUD. Base package `org.springy.som.modulith`. Java 17, Spring Boot 3.5.9-SNAPSHOT, Spring Modulith 1.4.14. The app listens on port 9080. The MongoDB database is `SOMDB`.

## Skills

Follow the skill. Do not copy its steps into this file. Repository skills live under `.grok/skills`. A tool without skill discovery should read the `SKILL.md` at the path below.

- **spring-modulith** ([`.grok/skills/spring-modulith/SKILL.md`](.grok/skills/spring-modulith/SKILL.md), `/spring-modulith`). Use it when structuring modules, fixing a boundary violation, choosing a Modulith feature, or writing a Modulith test. It owns feature choice, allowed dependencies, events, Moments, and the verification checklist. This repository imports `spring-modulith-bom` at 1.4.14 and leaves versions off the artifacts. The artifacts in use are `spring-modulith-starter-core`, `spring-modulith-starter-mongodb`, `spring-modulith-moments`, `spring-modulith-events-kafka`, runtime `spring-modulith-actuator`, and test-scoped `spring-modulith-starter-test`. The structural test class is `ModulithVerificationTest`. When the skill shows a sample class, keep this class. `starter-core` does not publish Moments. `spring-modulith-moments` is already on the classpath for memory expiration. Leave insight, Documenter, jMolecules, nested modules, `Type.OPEN`, and `@Externalized` out until that skill selects the row and the user asks.
- **java-code-reviewer** ([`.grok/skills/java-code-reviewer/SKILL.md`](.grok/skills/java-code-reviewer/SKILL.md), `/java-code-reviewer`). Use it when reviewing Java. It writes a findings report and leaves the code unchanged. Change the code only when the user asks. Judge the code against the Coding Standards in this file and against the Java release in `pom.xml`.
- **align-repository** ([`.grok/skills/align-repository/SKILL.md`](.grok/skills/align-repository/SKILL.md), `/align-repository`). Use it when aligning `springs-of-mud-server`, `springs-of-mud-designer`, `springs-of-mud-ui`, or `springs-of-mud-agentic-player` with this API. The report is the whole task. File it at `docs/alignment/<repository>_<YYYY-MM-DDTHHMMSS>.md`. The skill reads this file. Do not copy these standards back into the skill.

## Architecture

Direct subpackages of `org.springy.som.modulith` are the modules. `SomServerApplication` is the only type in the base package. It carries `@EnableAsync`, `@EnableScheduling`, `@Modulithic`, and `@SpringBootApplication`.

| Module | Boundary | Responsibility |
| --- | --- | --- |
| `domain` | `@ApplicationModule(type = CLOSED)` | World content. Capabilities stay nested subpackages. |
| `config` | default closed; types sit in the base package | Security and property beans. |
| `exception` | default closed; types sit in the base package | Shared API exceptions and `BaseApiExceptionHandler`. |
| `web` | default closed; types sit in the base package | Shared HTTP types, including `DeleteAllResponse`. |
| `knowledge` | `allowedDependencies = "exception"` | Agent world knowledge. |
| `memory` | `allowedDependencies = "exception"` | Agent working memory and retention. |
| `prompt` | `allowedDependencies = "exception"` | Agent prompts. |
| `agents` | `allowedDependencies = "exception"` | Agent registry documents. |

`domain` capabilities are `area`, `character`, `clazz`, `command`, `game`, `item`, `mobile`, `note`, `player`, `race`, `reset`, `room`, `shop`, `skill`, `special`, and `spell`. Each capability has an `api` package and an `internal` package. `DomainGuards` and `ServiceGuards` stay in the `domain` package. Do not promote a capability to its own module.

`knowledge`, `memory`, `prompt`, and `agents` use that same `api` / `internal` split. Each `api` package is `@NamedInterface("api")`. Those modules do not depend on `domain`, `config`, `web`, or each other.

Controllers are `@RestController` mappings under `/api/v1` that produce JSON. Domain controllers follow `AreaController`. The agent resources are:

| Resource | Methods | Collection |
| --- | --- | --- |
| knowledge | `GET`, `PUT /api/v1/agent-knowledge/{characterId}` | `AgentKnowledge` |
| memory | `GET`, `PUT /api/v1/agent-memory/{agentName}` | `Memories` |
| prompt | `GET /api/v1/agent-prompts`, `GET` and `PUT /api/v1/agent-prompts/{name}` | `AgentPrompts` |
| agents | `GET /api/v1/agents`, `GET` and `PUT /api/v1/agents/{name}` | `Agents` |

Memory retention is `som.memory.retention` (`P1D`). `MemoryExpirationListener` handles `HourHasPassed`. Knowledge, prompts, and agents have no expiration and no delete route. Application events published here stay in this process. Document templates are `src/main/resources/templates`.

## Coding Standards

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

## Build and test

From the repository root:

```powershell
.\mvnw.cmd test
.\mvnw.cmd "-Dtest=ModulithVerificationTest" test
```

`target/` is build output. Chain PowerShell commands with `;`.

## Configuration

`src/main/resources/application.yml` is the runtime config.

- The server port is 9080. The MongoDB database is `SOMDB`.
- `som.web-security.enabled` selects `WebSecurityConfig` when true and `DevSecurityConfig` when false. The checked-in value is false. When security is on, `/api/v1/**` requires `ROLE_somplayer`, `ROLE_game-master`, or `ROLE_somadmin`. Leave that filter chain as it is.
- `som.memory.retention` is `P1D`.
- Leave the files in `src/main/resources/templates` unchanged unless the user asks.
