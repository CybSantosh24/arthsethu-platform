@echo off
cls
echo.
echo ========================================
echo   ArthSethu Deployment Helper
echo ========================================
echo.
echo Your deployment files are ready!
echo.
echo ✅ render.yaml - Render configuration
echo ✅ vercel.json - Vercel configuration  
echo ✅ application-production.properties - Production settings
echo ✅ DEPLOYMENT_GUIDE.md - Complete instructions
echo.
echo ========================================
echo   DEPLOYMENT OPTIONS:
echo ========================================
echo.
echo 1. RENDER (Full Spring Boot App)
echo    - Go to: https://render.com
echo    - Connect GitHub repo: arthsethu-platform
echo    - Deploy as Blueprint
echo    - Result: https://arthsethu-platform.onrender.com
echo.
echo 2. VERCEL (Frontend Demos)
echo    - Go to: https://vercel.com  
echo    - Import GitHub repo: arthsethu-platform
echo    - Auto-deploy
echo    - Result: https://arthsethu-platform.vercel.app
echo.
echo ========================================
echo   RECOMMENDED: DEPLOY BOTH!
echo ========================================
echo.
echo Why both?
echo - Render: Full app for serious users
echo - Vercel: Quick demos for instant sharing
echo.
echo ========================================
echo   NEXT STEPS:
echo ========================================
echo.
echo 1. Commit and push these new files to GitHub
echo 2. Deploy to Render (5-10 minutes)
echo 3. Deploy to Vercel (2 minutes)  
echo 4. Share your live URLs!
echo.
echo Ready to commit deployment files? (Y/N)
set /p choice=
if /i "%choice%"=="Y" (
    echo.
    echo Committing deployment files...
    git add .
    git commit -m "Add deployment configurations for Render and Vercel"
    git push origin main
    echo.
    echo ✅ Deployment files pushed to GitHub!
    echo Now go to Render.com and Vercel.com to deploy.
) else (
    echo.
    echo No problem! Run this script again when ready.
)
echo.
pause