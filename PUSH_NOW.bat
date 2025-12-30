@echo off
echo ========================================
echo   PUSHING ARTHSETHU TO GITHUB NOW!
echo ========================================
echo.

echo Step 1: Pushing to GitHub...
git push -u origin main

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 🎉 SUCCESS! ARTHSETHU IS NOW LIVE!
    echo ========================================
    echo.
    echo Your repository is now available at:
    echo https://github.com/CybSantosh24/arthsethu-platform
    echo.
    echo IMMEDIATE DEMO ACCESS:
    echo 1. Go to your GitHub repository
    echo 2. Click on "DEMO_FIXED.html"
    echo 3. Click "Raw" to see the demo
    echo.
    echo ENABLE GITHUB PAGES:
    echo 1. Go to Settings in your repository
    echo 2. Scroll to "Pages" section
    echo 3. Source: "Deploy from a branch"
    echo 4. Branch: "main"
    echo 5. Your demo will be live at:
    echo    https://cybsantosh24.github.io/arthsethu-platform/DEMO_FIXED.html
    echo.
    echo WHAT'S INCLUDED:
    echo ✅ Complete Spring Boot Application
    echo ✅ Working AI CFO Demo
    echo ✅ Interactive Onboarding
    echo ✅ Business Dashboard
    echo ✅ Report Generation
    echo ✅ Professional README
    echo ✅ Security Best Practices
    echo.
) else (
    echo.
    echo ========================================
    echo ❌ PUSH FAILED
    echo ========================================
    echo.
    echo Possible issues:
    echo 1. Repository doesn't exist on GitHub
    echo 2. Network connection issue
    echo 3. Authentication problem
    echo.
    echo QUICK FIX:
    echo 1. Make sure repository "arthsethu-platform" exists on GitHub
    echo 2. Make sure it's public
    echo 3. Try running this script again
    echo.
)

echo.
pause