# Configuration

`src/main/resources/application.yml` is the runtime config.

- The server port is 9080. The MongoDB database is `SOMDB`.
- `som.web-security.enabled` selects `WebSecurityConfig` when true and `DevSecurityConfig` when false. The checked-in value is false. When security is on, `/api/v1/**` requires `ROLE_somplayer`, `ROLE_game-master`, or `ROLE_somadmin`. Leave that filter chain as it is.
- `som.memory.retention` is `P1D`.
- Leave the files in `src/main/resources/templates` unchanged unless the user asks.
