@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

set JAR=backend\target\wx-apartment-ledger-backend-0.0.1-SNAPSHOT.jar
if not exist "%JAR%" (
  echo 未找到可执行 JAR，请先运行 build.bat 进行构建。
  echo 路径: %JAR%
  pause
  exit /b 1
)

echo 正在启动房屋租赁管理...
echo 启动后请在浏览器打开: http://localhost:8080
echo 关闭本窗口即可停止程序。
echo.

:: 延迟 2 秒后打开浏览器
start "" cmd /c "timeout /t 2 /nobreak >nul && start http://localhost:8080"

C:\Users\Administrator\.jdks\corretto-21.0.6\bin\java.exe -jar "%JAR%"
pause
