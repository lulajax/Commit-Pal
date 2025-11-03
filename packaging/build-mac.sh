#!/bin/bash

# 切换到项目根目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR/.."

echo "================================"
echo "开始构建 macOS 安装包"
echo "================================"

echo ""
echo "[1/3] 清理旧的构建文件..."
mvn clean
if [ $? -ne 0 ]; then
    echo "✗ 清理失败"
    exit 1
fi

echo ""
echo "[2/3] 编译并打包应用..."
mvn package
if [ $? -ne 0 ]; then
    echo "✗ 编译失败"
    exit 1
fi

echo ""
echo "[3/3] 创建 macOS 安装程序..."
mvn jpackage:jpackage "-Djpackage.type=DMG"
if [ $? -ne 0 ]; then
    echo "✗ 打包失败"
    exit 1
fi

echo ""
echo "================================"
echo "✓ 构建完成！"
echo "================================"
echo ""
echo "安装文件位置: target/installer/"
echo ""

