@echo off
setlocal
cd /d "%~dp0"

echo === Agentic AI Demo - Setup ===

where java >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Java was not found on PATH. Install a Java 21+ JDK and try again.
    pause
    exit /b 1
)

where ollama >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Ollama was not found on PATH. Install it from https://ollama.com and try again.
    pause
    exit /b 1
)

echo.
echo [1/3] Checking Ollama model llama3.2:3b ...
ollama list | findstr /i "llama3.2:3b" >nul
if errorlevel 1 (
    echo Model not found locally, pulling it now ...
    ollama pull llama3.2:3b
) else (
    echo Model already present.
)

echo.
echo [2/3] Building Java backend (Maven, Spring Boot) ...
cd backend
call mvnw.cmd -q clean package -DskipTests
if errorlevel 1 (
    echo [ERROR] Backend build failed, see the Maven output above.
    cd ..
    pause
    exit /b 1
)
cd ..

echo.
echo [3/3] Setting up React frontend ...
cd frontend
call npm install
cd ..

echo.
echo Setup complete.
echo Double-click run.bat to launch the app.
pause
