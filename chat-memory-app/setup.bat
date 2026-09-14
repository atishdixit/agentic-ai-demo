@echo off
setlocal
cd /d "%~dp0"

echo === Chat Memory App - Setup ===

where java >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Java was not found on PATH. Install a Java 21+ JDK and try again.
    pause
    exit /b 1
)

where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Maven was not found on PATH. Install Maven 3.9+ and try again.
    pause
    exit /b 1
)

where node >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Node.js was not found on PATH. Install Node 18+ and try again.
    pause
    exit /b 1
)

where ollama >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Ollama was not found on PATH. Install it from https://ollama.com and try again.
    pause
    exit /b 1
)

where docker >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Docker was not found on PATH. Install Docker Desktop ^(used to run MySQL^) and try again.
    pause
    exit /b 1
)

if not exist .env (
    copy .env.example .env >nul
    echo Created .env from .env.example - edit it if you need different settings.
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
echo [2/4] Starting MySQL via Docker Compose ...
cd infra
docker compose up -d mysql
cd ..

echo.
echo [3/4] Building backend (Maven) ...
cd backend
call mvn -q install -DskipTests
if errorlevel 1 (
    echo [ERROR] Backend build failed, see the Maven output above.
    cd ..
    pause
    exit /b 1
)
cd ..

echo.
echo [4/4] Installing frontend dependencies (Angular) ...
cd frontend
call npm install
cd ..

echo.
echo Setup complete. Double-click run.bat to launch the app.
pause
