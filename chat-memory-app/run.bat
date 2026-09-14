@echo off
setlocal
cd /d "%~dp0"

echo === Chat Memory App - Starting ===

if not exist .env (
    copy .env.example .env >nul
)

for /f "usebackq eol=# tokens=1,2 delims==" %%A in (".env") do (
    if not "%%A"=="" set "%%A=%%B"
)

if "%API_PORT%"=="" set API_PORT=8081
if "%UI_PORT%"=="" set UI_PORT=4200

tasklist /FI "IMAGENAME eq ollama.exe" 2>nul | findstr /i "ollama.exe" >nul
if errorlevel 1 (
    echo Starting Ollama service ...
    start "Ollama" /min ollama serve
    timeout /t 3 >nul
) else (
    echo Ollama already running.
)

echo Starting MySQL via Docker Compose ...
cd infra
docker compose up -d mysql
cd ..

echo Starting backend (Spring Boot) on http://localhost:%API_PORT% ...
start "Chat App - Backend" cmd /k "cd /d "%~dp0backend" && java -jar target\chat-memory-app.jar"

echo Starting frontend (Angular) on http://localhost:%UI_PORT% ...
start "Chat App - Frontend" cmd /k "cd /d "%~dp0frontend" && npx ng serve --port %UI_PORT%"

echo Waiting for the servers to come up ...
timeout /t 15 >nul

start "" "http://localhost:%UI_PORT%"

echo.
echo Two windows were opened (backend + frontend). Close them to stop the app.
echo MySQL keeps running in Docker - stop it with: docker compose -f infra\docker-compose.yml down
