@echo off
echo ========================================
echo ArthSethu GitHub Setup Helper
echo ========================================
echo.

echo This script will help you push your project to GitHub
echo.

echo Step 1: Initialize Git repository
git init
echo.

echo Step 2: Add all files to Git
git add .
echo.

echo Step 3: Create initial commit
git commit -m "Initial commit: ArthSethu AI-Powered Business Intelligence Platform"
echo.

echo ========================================
echo NEXT STEPS (Manual):
echo ========================================
echo.
echo 1. Go to GitHub.com and create a new repository named 'arthsethu-platform'
echo 2. Copy the repository URL (e.g., https://github.com/yourusername/arthsethu-platform.git)
echo 3. Run these commands:
echo.
echo    git remote add origin YOUR_GITHUB_REPO_URL
echo    git branch -M main
echo    git push -u origin main
echo.
echo Example:
echo    git remote add origin https://github.com/yourusername/arthsethu-platform.git
echo    git branch -M main
echo    git push -u origin main
echo.
echo ========================================
echo DEMO READY!
echo ========================================
echo.
echo Your project is now ready for GitHub!
echo.
echo Quick Demo: Open DEMO_FIXED.html in your browser
echo Full Demo: Run run-demo.bat (requires Java)
echo.
pause