@echo off
setlocal
cd /d "%~dp0"

echo === Agentic AI Demo - Starting ===

tasklist /FI "IMAGENAME eq ollama.exe" 2>nul | findstr /i "ollama.exe" >nul
if errorlevel 1 (
    echo Starting Ollama service ...
    start "Ollama" /min ollama serve
    timeout /t 3 >nul
) else (
    echo Ollama already running.
)

echo Starting backend (FastAPI) on http://localhost:8000 ...
start "Agentic AI - Backend" cmd /k "cd /d "%~dp0backend" && call venv\Scripts\activate.bat && uvicorn main:app --reload --port 8000"

echo Starting frontend (React/Vite) on http://localhost:5173 ...
start "Agentic AI - Frontend" cmd /k "cd /d "%~dp0frontend" && npm run dev"

echo Waiting for the servers to come up ...
timeout /t 6 >nul

start "" "http://localhost:5173"

echo.
echo Two windows were opened (backend + frontend). Close them to stop the app.
