import os

import httpx

OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://localhost:11434")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "llama3.2:3b")


async def call_agent(system_prompt: str, user_prompt: str) -> str:
    """Send a single chat completion request to a local Ollama model."""
    payload = {
        "model": OLLAMA_MODEL,
        "messages": [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_prompt},
        ],
        "stream": False,
        # Keep responses short so the demo stays fast on CPU-only inference.
        "options": {"num_predict": 220},
    }
    async with httpx.AsyncClient(timeout=300) as client:
        response = await client.post(f"{OLLAMA_BASE_URL}/api/chat", json=payload)
        response.raise_for_status()
        data = response.json()
        return data["message"]["content"].strip()
