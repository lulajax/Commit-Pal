#!/usr/bin/env pwsh
# 环境检测脚本 - 检查打包所需的工具是否已安装

# 切换到项目根目录
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location (Split-Path -Parent $scriptDir)

Write-Host "================================" -ForegroundColor Cyan
Write-Host "环境检测 - Git Commit Helper" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

$allGood = $true

# 检测操作系统
Write-Host "操作系统信息:" -ForegroundColor Yellow
if ($IsWindows -or ($PSVersionTable.PSVersion.Major -lt 6) -or ($null -eq $IsWindows)) {
    Write-Host "  ✓ Windows" -ForegroundColor Green
    $os = "Windows"
} elseif ($IsMacOS) {
    Write-Host "  ✓ macOS" -ForegroundColor Green
    $os = "macOS"
} elseif ($IsLinux) {
    Write-Host "  ✓ Linux" -ForegroundColor Green
    $os = "Linux"
}
Write-Host ""

# 检查 Java
Write-Host "检查 Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String -Pattern 'version "(\d+)'
    if ($javaVersion -match 'version "(\d+)') {
        $version = [int]$matches[1]
        if ($version -ge 21) {
            Write-Host "  ✓ Java $version (满足要求: JDK 21+)" -ForegroundColor Green
        } else {
            Write-Host "  ✗ Java $version (需要: JDK 21+)" -ForegroundColor Red
            Write-Host "    请安装 JDK 21 或更高版本" -ForegroundColor Red
            Write-Host "    下载地址: https://adoptium.net/" -ForegroundColor Yellow
            $allGood = $false
        }
    }
    
    # 显示 JAVA_HOME
    if ($env:JAVA_HOME) {
        Write-Host "  JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Gray
    } else {
        Write-Host "  ! JAVA_HOME 未设置（可选）" -ForegroundColor Yellow
    }
} catch {
    Write-Host "  ✗ 未找到 Java" -ForegroundColor Red
    Write-Host "    请安装 JDK 21 或更高版本" -ForegroundColor Red
    Write-Host "    下载地址: https://adoptium.net/" -ForegroundColor Yellow
    $allGood = $false
}
Write-Host ""

# 检查 Maven
Write-Host "检查 Maven..." -ForegroundColor Yellow
try {
    $mvnVersion = mvn -version 2>&1 | Select-String -Pattern 'Apache Maven (\d+\.\d+)'
    if ($mvnVersion -match 'Apache Maven (\d+\.\d+)') {
        $version = $matches[1]
        Write-Host "  ✓ Maven $version" -ForegroundColor Green
    }
} catch {
    Write-Host "  ✗ 未找到 Maven" -ForegroundColor Red
    Write-Host "    请安装 Maven 3.6 或更高版本" -ForegroundColor Red
    Write-Host "    下载地址: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    $allGood = $false
}
Write-Host ""

# Windows 特定检查
if ($os -eq "Windows") {
    Write-Host "检查 Windows 打包工具..." -ForegroundColor Yellow
    
    # 检查 WiX Toolset
    $wixPath = $env:WIX
    if ($wixPath -and (Test-Path "$wixPath\bin\candle.exe")) {
        Write-Host "  ✓ WiX Toolset 已安装" -ForegroundColor Green
        Write-Host "    路径: $wixPath" -ForegroundColor Gray
    } else {
        # 尝试在 PATH 中查找
        $candleCmd = Get-Command candle.exe -ErrorAction SilentlyContinue
        if ($candleCmd) {
            Write-Host "  ✓ WiX Toolset 已安装" -ForegroundColor Green
        } else {
            Write-Host "  ✗ 未找到 WiX Toolset" -ForegroundColor Red
            Write-Host "    WiX Toolset 是创建 .exe 和 .msi 安装程序所必需的" -ForegroundColor Yellow
            Write-Host "    下载地址: https://github.com/wixtoolset/wix3/releases" -ForegroundColor Yellow
            Write-Host "    或使用 Chocolatey: choco install wixtoolset" -ForegroundColor Yellow
            $allGood = $false
        }
    }
    Write-Host ""
}

# macOS 特定检查
if ($os -eq "macOS") {
    Write-Host "检查 macOS 打包工具..." -ForegroundColor Yellow
    Write-Host "  ✓ macOS 自带 jpackage，无需额外工具" -ForegroundColor Green
    Write-Host ""
}

# 检查项目结构
Write-Host "检查项目结构..." -ForegroundColor Yellow
if (Test-Path "pom.xml") {
    Write-Host "  ✓ pom.xml 存在" -ForegroundColor Green
} else {
    Write-Host "  ✗ 未找到 pom.xml" -ForegroundColor Red
    $allGood = $false
}

if (Test-Path "src/main/java") {
    Write-Host "  ✓ 源代码目录存在" -ForegroundColor Green
} else {
    Write-Host "  ✗ 未找到源代码目录" -ForegroundColor Red
    $allGood = $false
}
Write-Host ""

# 检查图标文件（可选）
Write-Host "检查图标文件（可选）..." -ForegroundColor Yellow
$iconFound = $false
if ($os -eq "Windows" -and (Test-Path "src/main/resources/icon.ico")) {
    Write-Host "  ✓ Windows 图标 (icon.ico) 存在" -ForegroundColor Green
    $iconFound = $true
}
if ($os -eq "macOS" -and (Test-Path "src/main/resources/icon.icns")) {
    Write-Host "  ✓ macOS 图标 (icon.icns) 存在" -ForegroundColor Green
    $iconFound = $true
}
if (-not $iconFound) {
    Write-Host "  ! 未找到图标文件（将使用默认图标）" -ForegroundColor Yellow
    Write-Host "    参考 packaging/ICONS.md 了解如何添加自定义图标" -ForegroundColor Gray
}
Write-Host ""

# 总结
Write-Host "================================" -ForegroundColor Cyan
if ($allGood) {
    Write-Host "✓ 环境检测通过！" -ForegroundColor Green
    Write-Host "================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "你可以开始打包了：" -ForegroundColor Green
    if ($os -eq "Windows") {
        Write-Host "  cd packaging" -ForegroundColor Gray
        Write-Host "  .\build-portable.ps1   # 便携版（推荐，无需 WiX）" -ForegroundColor Cyan
        Write-Host "  .\build-windows.ps1    # Windows 安装程序" -ForegroundColor Cyan
        Write-Host "  .\build.ps1            # 自动检测" -ForegroundColor Cyan
    } elseif ($os -eq "macOS") {
        Write-Host "  cd packaging" -ForegroundColor Gray
        Write-Host "  ./build-mac.sh         # macOS 安装程序" -ForegroundColor Cyan
        Write-Host "  ./build.ps1            # 自动检测" -ForegroundColor Cyan
    }
} else {
    Write-Host "✗ 环境检测未通过" -ForegroundColor Red
    Write-Host "================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "请根据上面的提示修复问题后再尝试打包" -ForegroundColor Yellow
    exit 1
}

