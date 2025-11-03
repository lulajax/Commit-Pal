# 应用图标配置

为了让你的应用程序拥有专业的图标，你需要为不同平台准备相应格式的图标文件。

## 图标格式要求

### Windows
- **格式**: `.ico`
- **推荐尺寸**: 包含多个尺寸（16x16, 32x32, 48x48, 64x64, 128x128, 256x256）
- **位置**: `src/main/resources/icon.ico`

### macOS
- **格式**: `.icns`
- **推荐尺寸**: 包含多个尺寸（16x16 到 1024x1024）
- **位置**: `src/main/resources/icon.icns`

### Linux
- **格式**: `.png`
- **推荐尺寸**: 512x512 或更大
- **位置**: `src/main/resources/icon.png`

## 制作图标

### 方法 1：使用在线工具
1. **CloudConvert** (https://cloudconvert.com/)
   - 支持将 PNG 转换为 ICO、ICNS 格式
   - 免费且易用

2. **iConvert Icons** (https://iconverticons.com/)
   - 专门用于图标转换
   - 支持批量处理

### 方法 2：使用命令行工具

#### Windows - 使用 ImageMagick
```bash
# 安装 ImageMagick
choco install imagemagick

# 将 PNG 转换为 ICO（包含多个尺寸）
magick convert icon.png -define icon:auto-resize=256,128,64,48,32,16 icon.ico
```

#### macOS - 创建 ICNS
```bash
# 创建临时图标集目录
mkdir icon.iconset

# 生成不同尺寸的图标
sips -z 16 16     icon.png --out icon.iconset/icon_16x16.png
sips -z 32 32     icon.png --out icon.iconset/icon_16x16@2x.png
sips -z 32 32     icon.png --out icon.iconset/icon_32x32.png
sips -z 64 64     icon.png --out icon.iconset/icon_32x32@2x.png
sips -z 128 128   icon.png --out icon.iconset/icon_128x128.png
sips -z 256 256   icon.png --out icon.iconset/icon_128x128@2x.png
sips -z 256 256   icon.png --out icon.iconset/icon_256x256.png
sips -z 512 512   icon.png --out icon.iconset/icon_256x256@2x.png
sips -z 512 512   icon.png --out icon.iconset/icon_512x512.png
sips -z 1024 1024 icon.png --out icon.iconset/icon_512x512@2x.png

# 生成 ICNS 文件
iconutil -c icns icon.iconset

# 清理临时目录
rm -rf icon.iconset
```

## 配置图标路径

在 `pom.xml` 中配置图标路径（已配置，可根据实际情况修改）：

```xml
<plugin>
    <groupId>org.panteleyev</groupId>
    <artifactId>jpackage-maven-plugin</artifactId>
    <configuration>
        <!-- Windows 图标 -->
        <icon>${project.basedir}/src/main/resources/icon.ico</icon>
        
        <!-- 如果需要为不同平台指定不同图标 -->
        <!-- 可以在打包时通过命令行参数覆盖 -->
    </configuration>
</plugin>
```

## 不同平台打包时指定图标

### Windows 打包
```bash
mvn jpackage:jpackage -Djpackage.type=exe -Djpackage.icon=src/main/resources/icon.ico
```

### macOS 打包
```bash
mvn jpackage:jpackage -Djpackage.type=dmg -Djpackage.icon=src/main/resources/icon.icns
```

### Linux 打包
```bash
mvn jpackage:jpackage -Djpackage.type=deb -Djpackage.icon=src/main/resources/icon.png
```

## 设计建议

1. **简洁明了**：图标应该在小尺寸下仍然清晰可辨
2. **品牌一致**：保持与应用功能和品牌风格一致
3. **避免文字**：小图标中的文字难以阅读
4. **适当留白**：边缘保留一些空间，避免过于拥挤
5. **测试多尺寸**：确保在 16x16 和 256x256 下都好看

## 免费图标资源

- **Flaticon** (https://www.flaticon.com/) - 大量免费矢量图标
- **Icons8** (https://icons8.com/) - 多风格图标，提供免费版本
- **Font Awesome** (https://fontawesome.com/) - 经典图标库
- **Material Icons** (https://fonts.google.com/icons) - Google Material Design 图标

## 临时解决方案

如果暂时没有准备图标，可以：

1. 注释掉 `pom.xml` 中的图标配置：
```xml
<!-- <icon>${project.basedir}/src/main/resources/icon.ico</icon> -->
```

2. 使用系统默认图标进行打包

## 验证图标

打包完成后，检查生成的安装程序：
- **Windows**: 安装后检查桌面快捷方式和开始菜单
- **macOS**: 检查 Applications 文件夹中的应用图标
- **Linux**: 检查应用启动器图标

