# Chat Memory App

A small chat application that demonstrates **persistent conversation memory**: a local Ollama
model answers your messages, and every turn is stored in **MySQL** via Spring AI's
`JdbcChatMemoryRepository`, so the assistant remembers what you said earlier — even after a
server restart or a page reload — as long as you use the same username.

Built after reviewing the chat-memory pattern in
`code ai/spring-ai/section04/springai` (`ChatMemoryChatClientConfig`, `ChatMemoryController`),
which uses the same idea (`MessageWindowChatMemory` over `JdbcChatMemoryRepository`) against a
local H2 file. This app swaps H2 for real MySQL and wraps it in a full small application:
Spring Boot backend, Angular frontend, Docker infra, and one-click scripts.

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the design and [docs/SETUP.md](docs/SETUP.md)
for environment variables and prerequisites.

## How it works, in one sentence

Every message you send is tagged with your username as the **conversation id**; Spring AI's
`MessageWindowChatMemory` keeps the last N turns (configurable) for that id in MySQL, and
replays them to Ollama on every new message so it has context — that's the entire "memory"
mechanism, no vector database or embeddings involved.

## Quick start (Windows, one click)

```bash
setup.bat
```

Pulls the Ollama model if missing, starts MySQL via Docker Compose, builds the backend, and
installs frontend dependencies. Run once.

```bash
run.bat
```

Starts Ollama (if not running), MySQL (via Docker, if not running), the Spring Boot backend on
`http://localhost:8081`, and the Angular frontend on `http://localhost:4200`, then opens your
browser.

## Manual start

```bash
cd infra && docker compose up -d mysql && cd ..
cd backend && mvn install -DskipTests && java -jar target/chat-memory-app.jar
# in a second terminal
cd frontend && npm install && npx ng serve
```

## Docker (full stack)

```bash
cd infra
docker compose up --build
```

Runs MySQL, the backend, and the frontend all in containers (Ollama stays on the host — see
[docs/INFRASTRUCTURE notes in SETUP.md](docs/SETUP.md)).

## Project layout

```
backend/    Spring Boot app (com.example.chatapp) - single Maven module, kept small on purpose
frontend/   Angular 18, standalone components (core/features/shared)
infra/      docker-compose.yml + Dockerfiles + nginx.conf
docs/       ARCHITECTURE.md, SETUP.md
.env.example
setup.bat / run.bat
```

## Try it

1. Open the app, type a name (e.g. `atish`), and send a message like "My favorite color is blue."
2. Send a follow-up like "What's my favorite color?" — the assistant answers correctly because
   the first message is still in its context window (pulled from MySQL).
3. Reload the page and enter the same name again — your whole conversation reappears, loaded
   straight from the `ai_chat_memory` table.

## A note on speed

Every message is a real Ollama call on your own CPU/GPU. On CPU-only hardware, one reply can
take anywhere from ~20 to 90+ seconds — that's expected, not a bug.
