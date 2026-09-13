import { useState } from "react";

const API_URL = "http://localhost:8000/api/run";

const STAGES = [
  { key: "researcher", label: "1. Researcher Agent", hint: "Gathers key facts about the topic" },
  { key: "writer", label: "2. Writer Agent", hint: "Turns the research into a short article" },
  { key: "reviewer_feedback", label: "3. Reviewer Agent – Feedback", hint: "Critiques the draft" },
  { key: "final_article", label: "3. Reviewer Agent – Final Article", hint: "Polished, final output" },
];

export default function App() {
  const [topic, setTopic] = useState("Why sea otters matter for coastal ecosystems");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);

  async function handleRun(e) {
    e.preventDefault();
    if (!topic.trim() || loading) return;
    setLoading(true);
    setError("");
    setResult(null);
    try {
      const res = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ topic }),
      });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.detail || `Request failed (${res.status})`);
      }
      setResult(await res.json());
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page">
      <header>
        <h1>Agentic AI Demo</h1>
        <p className="subtitle">
          Researcher &rarr; Writer &rarr; Reviewer, three local Ollama agents working together.
        </p>
      </header>

      <form onSubmit={handleRun} className="topic-form">
        <input
          type="text"
          value={topic}
          onChange={(e) => setTopic(e.target.value)}
          placeholder="Enter a topic..."
          disabled={loading}
        />
        <button type="submit" disabled={loading}>
          {loading ? "Running agents..." : "Run"}
        </button>
      </form>

      {error && <div className="error">{error}</div>}

      {loading && (
        <p className="loading-note">
          Local model is thinking through 3 agent steps, this can take a little while...
        </p>
      )}

      {result && (
        <div className="stages">
          {STAGES.map((stage) => (
            <section key={stage.key} className="card">
              <h2>{stage.label}</h2>
              <p className="hint">{stage.hint}</p>
              <pre>{result[stage.key]}</pre>
            </section>
          ))}
        </div>
      )}
    </div>
  );
}
