# Architecture

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
