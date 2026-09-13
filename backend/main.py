from dotenv import load_dotenv

load_dotenv()

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

from orchestrator import run_pipeline

app = FastAPI(title="Agentic AI Demo")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_methods=["*"],
    allow_headers=["*"],
)


class RunRequest(BaseModel):
    topic: str


@app.get("/api/health")
async def health():
    return {"status": "ok"}


@app.post("/api/run")
async def run(request: RunRequest):
    topic = request.topic.strip()
    if not topic:
        raise HTTPException(status_code=400, detail="topic must not be empty")
    try:
        return await run_pipeline(topic)
    except Exception as exc:
        raise HTTPException(
            status_code=502,
            detail=(
                "Agent pipeline failed (is Ollama running? `ollama serve`): "
                f"{type(exc).__name__}: {exc}"
            ),
        )
