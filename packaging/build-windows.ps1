#!/usr/bin/env pwsh

Write-Host "================================" -ForegroundColor Green
Write-Host "开始构建 Windows 安装包" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""

# 切换到项目根目录
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location (Split-Path -Parent $scriptDir)

Write-Host "[1/3] 清理旧的构建文件..." -ForegroundColor Yellow
mvn clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ 清理失败" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2/3] 编译并打包应用..." -ForegroundColor Yellow
mvn package
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ 编译失败" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[3/3] 创建 Windows 安装程序..." -ForegroundColor Yellow
mvn jpackage:jpackage `"-Djpackage.type=EXE`" `"-Djpackage.winMenu=true`" `"-Djpackage.winShortcut=true`" `"-Djpackage.winDirChooser=true`" `"-Djpackage.winMenuGroup=Commit Pal`"
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ 打包失败" -ForegroundColor Red
    Write-Host ""
    Write-Host "提示：需要安装 WiX Toolset" -ForegroundColor Yellow
    Write-Host "  choco install wixtoolset" -ForegroundColor Yellow
    Write-Host "  或访问: https://wixtoolset.org/" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "================================" -ForegroundColor Green
Write-Host "✓ 构建成功！" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "安装文件位置: " -NoNewline
Write-Host "target\installer\" -ForegroundColor Cyan
Write-Host ""
