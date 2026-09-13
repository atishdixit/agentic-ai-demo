@echo off
setlocal
cd /d "%~dp0"

echo === Agentic AI Demo - Setup ===

set PY=py
where py >nul 2>nul
if errorlevel 1 (
    set PY=python
)

where ollama >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Ollama was not found on PATH. Install it from https://ollama.com and try again.
    pause
    exit /b 1
)

echo.
echo [1/4] Checking Ollama model llama3.2:3b ...
ollama list | findstr /i "llama3.2:3b" >nul
if errorlevel 1 (
    echo Model not found locally, pulling it now ...
    ollama pull llama3.2:3b
) else (
    echo Model already present.
)

echo.
echo [2/4] Setting up Python backend ...
cd backend
if not exist venv (
    %PY% -m venv venv
)
call venv\Scripts\activate.bat
pip install --upgrade pip >nul
pip install -r requirements.txt
if not exist .env (
    copy .env.example .env >nul
)
call venv\Scripts\deactivate.bat
cd ..

echo.
echo [3/4] Setting up React frontend ...
cd frontend
call npm install
cd ..

echo.
echo [4/4] Setup complete.
echo Double-click run.bat to launch the app.
pause
