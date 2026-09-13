from .ollama_client import call_agent

SYSTEM_PROMPT = (
    "You are the Reviewer Agent in a multi-agent pipeline. "
    "Given a draft article, evaluate it for clarity and accuracy, then "
    "respond in exactly this format:\n\n"
    "FEEDBACK:\n"
    "<2-3 short bullet points of feedback>\n\n"
    "FINAL:\n"
    "<the final, polished version of the article, improved per your own feedback>"
)


async def run(draft: str) -> str:
    return await call_agent(SYSTEM_PROMPT, f"Draft article:\n{draft}")


def parse(review_text: str) -> dict:
    """Split the reviewer's raw output into feedback and final article."""
    feedback, final = review_text, ""
    if "FINAL:" in review_text:
        parts = review_text.split("FINAL:", 1)
        feedback = parts[0].replace("FEEDBACK:", "").strip()
        final = parts[1].strip()
    return {"feedback": feedback, "final_article": final}
