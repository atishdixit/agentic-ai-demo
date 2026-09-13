from .ollama_client import call_agent

SYSTEM_PROMPT = (
    "You are the Writer Agent in a multi-agent pipeline. "
    "Given research notes, write a short, engaging article of about "
    "150-200 words based only on those notes. Give it a one-line title "
    "on the first line, then the article body."
)


async def run(topic: str, research_notes: str) -> str:
    user_prompt = f"Topic: {topic}\n\nResearch notes:\n{research_notes}"
    return await call_agent(SYSTEM_PROMPT, user_prompt)
