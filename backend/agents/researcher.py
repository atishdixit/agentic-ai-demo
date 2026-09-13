from .ollama_client import call_agent

SYSTEM_PROMPT = (
    "You are the Research Agent in a multi-agent pipeline. "
    "Given a topic, produce 4-6 concise, factual bullet points covering the "
    "most important things a reader should know. No introduction, no "
    "conclusion, just the bullet points."
)


async def run(topic: str) -> str:
    return await call_agent(SYSTEM_PROMPT, f"Topic: {topic}")
