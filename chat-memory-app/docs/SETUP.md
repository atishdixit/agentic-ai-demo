# Setup

## Prerequisites

- **Java 21+** (JDK), **Maven 3.9+**
- **Node.js 18+** and npm
- **[Ollama](https://ollama.com)**, with `llama3.2:3b` pulled (`setup.bat` pulls it automatically
  if missing)
- **Docker Desktop**, used to run MySQL (see "Running without Docker" below if you'd rather
  point at an existing MySQL instance instead)

## Environment variables

Copy `.env.example` to `.env` at the repo root. Loaded by the backend automatically
(`spring-dotenv`) and by `docker-compose` (`env_file`-style variable substitution).

| Variable            | Default          | Meaning                                         |
|----------------------|-------------------|---------------------------------------------------|
| `MYSQL_HOST`          | `localhost`       | Where MySQL is listening (native run)             |
| `MYSQL_PORT`          | `3306`            | MySQL port                                        |
| `MYSQL_DATABASE`      | `chatmemory`      | Database name (created automatically)             |
| `MYSQL_USER`          | `chatapp`         | App's DB user                                     |
| `MYSQL_PASSWORD`      | `chatapp`         | App's DB password                                 |
| `MYSQL_ROOT_PASSWORD` | `root`            | Only used by `docker-compose` to init the container |
| `OLLAMA_BASE_URL`     | `http://localhost:11434` | Where Ollama is listening                  |
| `OLLAMA_MODEL`        | `llama3.2:3b`     | Model name                                        |
| `CHAT_TEMPERATURE`    | `0.6`             | Portable `ChatOptions` temperature                |
| `CHAT_MAX_TOKENS`     | `300`             | Ceiling on one reply's length                     |
| `CHAT_MEMORY_WINDOW`  | `10`              | How many recent messages the model is shown per turn |
| `API_PORT`            | `8081`            | Backend port                                      |
| `UI_PORT`             | `4200`            | Frontend port                                     |
| `UI_ORIGIN`           | `http://localhost:4200` | Allowed CORS origin on the backend           |

## How chat memory works (and how to inspect it)

`ChatMemoryConfig` wires a `MessageWindowChatMemory` (keeps the last `CHAT_MEMORY_WINDOW`
messages per conversation) over Spring AI's `JdbcChatMemoryRepository`, which auto-creates and
uses an `ai_chat_memory` table (`spring.ai.chat.memory.repository.jdbc.initialize-schema=always`
in `application.yml`). The conversation id is simply the username you type into the UI.

To look at stored history directly:

```sql
-- via any MySQL client, e.g. mysql -h localhost -u chatapp -p chatmemory
SELECT conversation_id, type, content, timestamp FROM ai_chat_memory ORDER BY timestamp;
```

## Running without Docker

If you already have MySQL running natively, skip the `docker compose up -d mysql` step in
`setup.bat`/`run.bat` and just make sure `.env`'s `MYSQL_*` variables point at it, and that the
database/user/password in `.env` already exist (Spring AI creates the *table*, not the
database/user — `createDatabaseIfNotExist=true` in the JDBC URL handles the database itself,
but the user must already exist with privileges on it).

## A known limitation hit while building this: Docker Desktop in this environment

While building this app, Docker Desktop would not fully start in this sandboxed environment —
`docker ps` never returned even after multiple clean restarts, and `wsl --list --verbose` showed
the `docker-desktop` WSL2 distro stuck in a `Stopped` state (despite `wsl -d docker-desktop`
itself working, so it isn't a blanket virtualization block — Docker Desktop's own engine simply
never came up here). **This is an environment limitation, not a bug in the app**: the
`docker-compose.yml` / Dockerfiles are written the same way as the two prior projects this
session (`employee-support-system`, `ai-work`'s agentic demo), which do work under normal
Docker Desktop. On a machine where Docker starts normally, `setup.bat` / `docker compose up`
should work as documented above.

To still get real confidence the chat-memory logic itself is correct without a working Docker
here, the backend was run directly against a temporary in-memory H2 database (H2 added
temporarily to `pom.xml`, then removed again before committing - the shipped `pom.xml` only
ever depends on `mysql-connector-j`) and exercised end-to-end:

```
POST /api/chat/messages {"username":"testuser","message":"My favorite color is teal. Just remember that."}
→ "I've taken note that your favorite color is teal. ..."

POST /api/chat/messages {"username":"testuser","message":"What is my favorite color?"}
→ "You told me earlier that your favorite color is teal."

GET /api/chat/messages?username=testuser
→ all 4 messages returned in order
```

This proves `ChatMemoryConfig`/`ChatController`'s use of Spring AI's JDBC chat memory API is
correct. The MySQL-specific path itself relies on `MysqlChatMemoryRepositoryDialect` and
`schema-mysql.sql`, which are confirmed present in `spring-ai-model-chat-memory-repository-jdbc:1.1.2`
(the same auto-detection mechanism that picked H2 automatically here would pick MySQL from a
real MySQL `DataSource` the same way) — but that specific path was not exercised live against a
running MySQL server in this session. If you hit anything MySQL-specific once Docker (or a
native MySQL install) is available, please report it.
