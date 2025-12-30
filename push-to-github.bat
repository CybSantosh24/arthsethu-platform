@echo off
echo ========================================
echo Pushing ArthSethu to GitHub
echo ========================================
echo.

echo Step 1: Setting up remote repository...
git remote add origin https://github.com/CybSantosh24/arthsethu-platform.git

echo.
echo Step 2: Setting branch to main...
git branch -M main

echo.
echo Step 3: Pushing to GitHub...
git push -u origin main

echo.
echo ========================================
echo SUCCESS! Your project is now on GitHub!
echo ========================================
echo.
echo Repository URL: https://github.com/CybSantosh24/arthsethu-platform
echo Live Demo: https://cybsantosh24.github.io/arthsethu-platform/DEMO_FIXED.html
echo.
echo Next steps:
echo 1. Go to GitHub.com and create a repository named 'arthsethu-platform'
echo 2. Make it public so others can see your demo
echo 3. Enable GitHub Pages in repository settings
echo.
pause