@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo ========================================
echo   房屋租赁管理 - 一键构建
echo ========================================

:: 1. 构建前端
echo.
echo [1/3] 构建前端...
cd frontend
if not exist "node_modules" (
  echo 正在安装前端依赖...
  call npm install
)
call npm run build
if errorlevel 1 (
  echo 前端构建失败
  cd ..
  pause
  exit /b 1
)
cd ..

:: 2. 复制前端产物到后端 static
echo.
echo [2/3] 复制前端到后端静态资源...
if not exist "backend\src\main\resources\static" mkdir "backend\src\main\resources\static"
xcopy /E /Y /Q "frontend\dist\*" "backend\src\main\resources\static\"
if errorlevel 1 (
  echo 复制失败
  pause
  exit /b 1
)

:: 3. 打包后端（含前端）
echo.
echo [3/3] 打包后端 JAR...
cd backend
call mvn -q -DskipTests package
if errorlevel 1 (
  echo 后端打包失败
  cd ..
  pause
  exit /b 1
)
cd ..

echo.
echo ========================================
echo   构建完成
echo   运行 start.bat 启动程序
echo ========================================
pause
