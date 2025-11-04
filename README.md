<div align="center">
  <img src="https://github.com/lulajax/Commit-Pal/blob/main/src/main/resources/icon.png" alt="Commit Pal Logo" width="150"/>
  <h1>Commit Pal</h1>
  <p><strong>您的智能 Git 提交伙伴，让每一次提交都精准而优雅。</strong></p>
  
  <p>
    <a href="https://github.com/lulajax/Commit-Pal/releases"><img src="https://img.shields.io/github/v/release/Commit-Pal/commit-pal?style=for-the-badge&logo=github" alt="版本"></a>
    <a href="#"><img src="https://img.shields.io/badge/JavaFX-21-orange?style=for-the-badge&logo=oracle" alt="JavaFX"></a>
  </p>
</div>

---

**Commit Pal** 是一款基于 JavaFX 的智能 Git 提交信息生成工具。它旨在利用 AI 的强大能力，将繁琐的 `git commit` 信息撰写过程变得智能、高效且充满乐趣。它能自动分析您的代码变更，并结合您为每个项目量身定制的提示词（Prompt），生成高质量、符合规范的提交日志。

## ✨ 核心功能

- 🤖 **AI 驱动生成**：根据暂存区代码变更，智能生成符合规范的提交信息。
- 📈 **提交报告分析**：选择任意时间段，一键生成精美的提交报告，轻松完成周报和项目总结。
- 🗂️ **多项目管理**：无缝切换和管理多个 Git 仓库，告别混乱。
- 🎨 **高度可定制**：无论是提交信息还是分析报告，都可通过自定义 Prompt 模板满足团队规范。
- 🌐 **多模型与代理**：支持多种 LLM 提供商（OpenAI、Claude 等）并内置 HTTP 代理支持。
- 💻 **现代化 UI**：基于 [AtlantaFX](https://github.com/mkpaz/atlantafx) 主题，提供流畅、美观的用户体验。
- 💾 **配置持久化**：所有配置自动保存，开箱即用。

## 📸 应用截图

![Commit Pal Screenshot](https://github.com/lulajax/Commit-Pal/blob/main/src/main/resources/home.png)

## 🚀 快速开始

### 环境要求
- JDK 21 或更高版本
- Maven 3.6+

### 启动方式

#### 方式一：使用 Maven 运行 (推荐)
```bash
# 克隆项目
git clone https://github.com/lulajax/Commit-Pal.git
cd Commit-Pal

# 运行应用
mvn clean javafx:run
```
<details>
  <summary><b>Windows PowerShell 环境设置 (如果需要)</b></summary>
  
  ```powershell
  # 设置 JDK 环境变量 (请替换为您的实际路径)
  $env:JAVA_HOME = "C:\path\to\your\jdk-21"
  $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
  ```
</details>

#### 方式二：在 IDE 中运行
- **IntelliJ IDEA**: 直接导入为 Maven 项目，找到 `MainApplication.java` 右键运行。
- **VSCode**: 确保已安装 "Extension Pack for Java"，然后按 `F5` 启动调试。


## 🔧 使用指南

### 1. 首次配置
首次运行时，应用会在用户主目录下创建配置文件 `~/.commit-pal/config.json`。
1. 在右侧的 **LLM Settings** 中填入您的 API Key、模型名称等信息。
2. 如果需要，在 **Proxy Settings** 中配置您的代理。
3. 点击 **Save Settings** 保存。

### 2. 生成提交信息
1. 点击左侧 **"+"** 按钮添加您的 Git 项目。
2. 在 **Git Commit** 标签页，点击 **Refresh** 获取暂存区变更。
3. 点击 **Generate** 生成提交信息。
4. 编辑后，可 **Copy** 或直接 **Commit**。

### 3. 生成提交报告
1. 切换到 **Commit Report** 标签页。
2. 选择开始和结束日期，点击 **Fetch Commit Logs**。
3. 在右侧 **Commit Report Prompt** 中调整模板。
4. 点击 **Generate** 生成报告，然后 **Copy** 到剪贴板。


## 🛠️ 技术栈

- **核心框架**: JavaFX 21
- **UI 主题**: [AtlantaFX](https://github.com/mkpaz/atlantafx)
- **Git 操作**: [JGit](https://www.eclipse.org/jgit/)
- **数据处理**: [Gson](https://github.com/google/gson)
- **HTTP通信**: [Hutool](https://hutool.cn/)
- **构建工具**: Maven


## 📦 打包与分发

项目支持使用 `jpackage` 生成原生安装程序 (Windows, macOS, Linux)。所有脚本和文档都在 `packaging/` 目录中。

**👉 查看 [packaging/README.md](packaging/README.md) 获取完整的打包指南。**

**Windows 快速打包 (便携版):**
```powershell
cd packaging
.\build-portable.ps1
```

## 🤝 贡献

欢迎任何形式的贡献！无论是提交 Issue、请求新功能还是提交 Pull Request。

1. **Fork** 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 **Pull Request**

## 📄 许可证

本项目基于 [MIT License](LICENSE) 授权。
