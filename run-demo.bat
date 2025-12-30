@echo off
echo ========================================
echo ArthSethu Demo Launcher
echo ========================================
echo.

echo Checking Java installation...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher and add it to your PATH
    pause
    exit /b 1
)

echo.
echo Starting ArthSethu application...
echo.
echo The application will be available at:
echo http://localhost:8080/arthsethu
echo.
echo Demo URLs:
echo - Home: http://localhost:8080/arthsethu/demo
echo - Onboarding: http://localhost:8080/arthsethu/demo/onboarding  
echo - AI CFO: http://localhost:8080/arthsethu/demo/ai-cfo
echo - Dashboard: http://localhost:8080/arthsethu/demo/dashboard
echo - Reports: http://localhost:8080/arthsethu/demo/reports
echo.

REM Try to run with Maven wrapper first
if exist "mvnw.cmd" (
    echo Using Maven wrapper...
    call mvnw.cmd spring-boot:run
) else (
    echo Maven wrapper not found, trying with Maven...
    mvn spring-boot:run
)

if %errorlevel% neq 0 (
    echo.
    echo ========================================
    echo ERROR: Could not start the application
    echo ========================================
    echo.
    echo Alternative: Open demo-standalone.html in your browser
    echo This file contains working demos that don't require the server
    echo.
    pause
)