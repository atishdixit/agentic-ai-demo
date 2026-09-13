from agents import researcher, reviewer, writer


async def run_pipeline(topic: str) -> dict:
    """Run the Researcher -> Writer -> Reviewer agent pipeline in sequence."""
    research_notes = await researcher.run(topic)
    draft = await writer.run(topic, research_notes)
    review_raw = await reviewer.run(draft)
    review = reviewer.parse(review_raw)

    return {
        "topic": topic,
        "researcher": research_notes,
        "writer": draft,
        "reviewer_feedback": review["feedback"],
        "final_article": review["final_article"] or draft,
    }
