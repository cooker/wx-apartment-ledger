@echo off
chcp 65001 >nul
cd /d "%~dp0\frontend"

echo 正在安装依赖并打包 Electron exe...
if not exist "node_modules" call npm install
call npm run electron:build
if errorlevel 1 (
  echo 打包失败
  pause
  exit /b 1
)

echo.
echo 打包完成。安装包位置: frontend\release\
echo 请先启动后端（运行 start.bat 或 java -jar backend\...），再运行 exe。
pause
