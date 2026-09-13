# Agentic AI Demo — Researcher → Writer → Reviewer

A very small example of an **agentic AI application**: three cooperating
agents (Researcher, Writer, Reviewer) that hand work off to each other in
sequence to turn a single topic into a reviewed, polished article — all
running on a **local Ollama model**, no cloud API keys needed.

See [docs/flow-diagram.md](docs/flow-diagram.md) for the full flow diagram
and an explanation of how the agents talk to each other.

## Tech stack

| Layer          | Tech                                    |
|----------------|------------------------------------------|
| AI model       | Ollama, running locally (`llama3.2:3b`)  |
| Backend        | Java 21, Spring Boot 3, Maven            |
| Frontend       | React (Vite)                             |
| Infrastructure | Docker Compose (optional)                |
| Quick start    | Windows `.bat` scripts                   |

## Prerequisites

- [Ollama](https://ollama.com) installed and on your PATH.
- Java 21+ (JDK).
- Node.js 18+

The `llama3.2:3b` model will be pulled automatically by `setup.bat` if you
don't already have it (`ollama pull llama3.2:3b`).

No separate Maven install is required — the project ships with the Maven
Wrapper (`backend/mvnw.cmd`), which downloads the right Maven version on
first use.

## Quick start (Windows, one click)

```bash
setup.bat
```

Run this once. It builds the Spring Boot backend (downloading its Maven
dependencies), installs frontend dependencies, and pulls the Ollama model
if needed.

```bash
run.bat
```

Run this any time you want to use the app. It starts Ollama (if not
already running), the Spring Boot backend on `http://localhost:8000`, the
React frontend on `http://localhost:5173`, and opens it in your browser.

## Running with Docker instead

An alternative, containerized setup is provided in [infra/docker-compose.yml](infra/docker-compose.yml).
It runs the backend and frontend in containers, connecting to your
**host machine's** Ollama instance:

```bash
cd infra
docker compose up --build
```

## Project layout

```
backend/
  src/main/java/com/example/agenticdemo/
    agent/
      ResearcherAgent.java     # Agent 1: gathers research notes on the topic
      WriterAgent.java          # Agent 2: drafts a short article from the notes
      ReviewerAgent.java         # Agent 3: critiques the draft and finalizes it
      PipelineOrchestrator.java # Runs the 3 agents in sequence
    ollama/OllamaClient.java     # Shared helper that calls the local Ollama API
    web/RunController.java       # REST controller exposing POST /api/run
  src/main/resources/application.properties
  pom.xml
frontend/
  src/App.jsx            # UI: enter a topic, see each agent's output
infra/
  docker-compose.yml    # Optional containerized setup
docs/
  flow-diagram.md        # Mermaid diagram of the agent pipeline
setup.bat                # One-time install
run.bat                  # One-click start
```

## How the agents work together

1. You type a **topic** in the frontend.
2. **Researcher Agent** turns it into a handful of factual bullet points.
3. **Writer Agent** turns those notes into a short draft article.
4. **Reviewer Agent** critiques the draft and produces the final version.
5. All three outputs are shown in the UI so you can see each step of the
   pipeline, not just the end result.

Each agent is just the *same* local model called with a different system
prompt/role — the "multi-agent" behavior comes from the orchestration
(sequential hand-off of state), not from needing multiple different
models.

## A note on speed

Every agent step is a real call to your local Ollama model, run on your
own CPU/GPU. On modest hardware (CPU-only inference), a full 3-agent run
can take a couple of minutes — that's expected, not a bug. The backend
caps each agent's response length (`num_predict`) to keep things
reasonably fast.
