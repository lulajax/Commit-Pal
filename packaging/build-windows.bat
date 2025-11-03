@echo off
cd /d "%~dp0\.."

echo ================================
echo 开始构建 Windows 安装包
echo ================================

echo.
echo [1/3] 清理旧的构建文件...
call mvn clean
if %ERRORLEVEL% neq 0 (
    echo 清理失败
    pause
    exit /b 1
)

echo.
echo [2/3] 编译并打包应用...
call mvn package
if %ERRORLEVEL% neq 0 (
    echo 编译失败
    pause
    exit /b 1
)

echo.
echo [3/3] 创建 Windows 安装程序...
call mvn jpackage:jpackage -Djpackage.type=exe
if %ERRORLEVEL% neq 0 (
    echo 打包失败
    echo 提示：需要安装 WiX Toolset
    echo   choco install wixtoolset
    echo   或访问: https://wixtoolset.org/
    pause
    exit /b 1
)

echo.
echo ================================
echo 构建完成！
echo 安装文件位置: target\installer\
echo ================================
pause

