---
name: align-repository
description: >-
  Reports the proposed changes that would align springs-of-mud-server,
  springs-of-mud-designer, springs-of-mud-ui, or springs-of-mud-agentic-player
  with som-server-modulith. The report is the whole task. Use when the user
  runs /align-repository or asks to align one of those repositories with
  this modulith.
---

# Align repository

Write a report of the proposed changes that would align the named target with this repository, and file it at the path under Report. That report is the whole task. Do not edit this repository or the target.

Work from `som-server-modulith`, the repository that contains this skill. It is the Spring Boot Modulith REST API for Springs of MUD. Read `pom.xml` and `org.springy.som.modulith.SomServerApplication` before naming a contract. Module structure is `.grok/skills/spring-modulith/SKILL.md`. Java practices are `.grok/skills/java-code-reviewer/SKILL.md`. Read `AGENTS.md` and `AGENTS.override.md` in this repository when they exist. Do not copy those standards into this skill or into a second checklist.

## Target repositories

The valid targets are:

- `springs-of-mud-server` — It loads content from this API.
- `springs-of-mud-designer` — Angular content designer. It authors and saves content through this API.
- `springs-of-mud-ui` — It does not author world content.
- `springs-of-mud-agentic-player` — It logs into the game server.

If the user did not name one of these four, ask which target to report. A repository that is not in this list is not a target.

Locate the checkout from the path the user gave or from a nearby project directory. Do not assume one absolute path. If the checkout is missing, ask for it. Read the target's `AGENTS.md` and `AGENTS.override.md` when they exist, and read a skill those files name when the comparison uses that convention.

## This repository

Prefer the code when it disagrees with this list.

- The application class is `org.springy.som.modulith.SomServerApplication`. Build and test with `.\mvnw.cmd`.
- `org.springy.som.modulith.domain` is one closed application module. Each capability is a direct subpackage. Types other code may use are in that capability's `api` package, annotated `@NamedInterface("api")`. Controllers, documents, repositories, and services are in `internal`.
- Controllers are `@RestController` mappings under `/api/v1` that produce JSON. Reads return view types. Creates and updates accept document types. Shared HTTP types are in `org.springy.som.modulith.exception` and `org.springy.som.modulith.web`. Config is in `org.springy.som.modulith.config`.
- MongoDB persistence uses the database named in `src/main/resources/application.yml`. Document templates are `src/main/resources/templates`.
- Tests are JUnit under `src/test/java`. Controller slices use `@WebMvcTest`. The structural test is `ModulithVerificationTest`.
- Application events published with `ApplicationEventPublisher` stay inside this process. A target does not subscribe to them.
- Skip `target/`.

A published contract is a controller mapping, a view or document field, validation on that body, or a status the controller returns. An internal type that no controller, view, document, or event exposes is not a contract for the target.

## Find the gaps

Compare the HTTP calls, models, and saved documents the target actually uses with the published contracts. Use the target's architecture map for its source roots. Skip `node_modules`, `dist`, and build output.

An endpoint the target never calls is not a gap. Record a target gap when a call, model, or saved document will send, store, or require a path, status, or field this API does not provide, or when it drops a persisted field the target's standard says to keep. Record a gap in this repository when a published contract breaks `.grok/skills/spring-modulith/SKILL.md` or `.grok/skills/java-code-reviewer/SKILL.md` and the target depends on that contract. Do not review unrelated Java.

A proposal preserves behavior except where a standard requires the change. Propose the change in the target. Propose a change in this repository only when the published contract is the part that breaks the standard. For a move in this repository, record the type, the destination, and each importer here. When that move changes a path a test command names, record the command update as its own proposed change.

Leave `application.yml` and the template documents unchanged unless a standard requires the change. Omit passwords, client secrets, keystore material, JWTs, `Authorization` headers, and `ENC(...)` values from the report.

## Report

File the report in this repository at `docs/alignment`. Create the folder when it is missing. Name the file with the target repository and the local time it is filed, in 24-hour form: `<repository>_<YYYY-MM-DDTHHMMSS>.md`.

For each recorded gap, name the file, the standard or contract it addresses, and the proposed change, including which repository the change is in. If the comparison finds no gap, say there is nothing to propose. If a gap cannot be stated precisely, name the file and what is still unknown.
