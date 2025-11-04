# AI 对话功能变更日志

## 新增功能：AI 智能对话助手

**实现日期**: 2025-11-04

### 📝 变更摘要

为 Commit-Pal 添加了全新的 AI 对话功能，基于 LangChain4j 框架实现，能够通过自然语言交互自动识别用户意图并调用相应的工具。

### 🎯 主要功能

1. **智能对话界面**
   - 现代化的聊天 UI
   - 支持多轮对话
   - 实时响应
   - 对话历史记忆（最多 20 条消息）

2. **自动工具调用**
   - 生成 Git commit 信息
   - 生成指定时间段的提交报告
   - 查看暂存区状态
   - 查看提交历史

3. **自然语言理解**
   - 自动识别用户意图
   - 支持中文对话
   - 智能参数提取

### 📦 新增文件

#### 核心服务类
- `src/main/java/com/junjie/githelper/service/ToolService.java`
  - 定义了 4 个 @Tool 注解的工具方法
  - 与 GitService 集成，提供 Git 操作能力

- `src/main/java/com/junjie/githelper/service/ChatAssistant.java`
  - LangChain4j AI Services 接口
  - 定义系统提示词和对话行为

#### 控制器
- `src/main/java/com/junjie/githelper/controller/ChatViewController.java`
  - 管理对话窗口的 UI 逻辑
  - 集成 LangChain4j 的 AiServices
  - 处理消息发送和接收

#### UI 资源
- `src/main/resources/com/junjie/githelper/chat-view.fxml`
  - 对话窗口的 FXML 布局

- `src/main/resources/com/junjie/githelper/chat-style.css`
  - 对话窗口的样式定义

#### 文档
- `docs/AI_CHAT_GUIDE.md`
  - 详细的使用指南
  - 包含示例和常见问题

- `CHANGELOG_AI_CHAT.md`
  - 本变更日志

### 🔧 修改的文件

#### 依赖配置
- `pom.xml`
  - 添加 LangChain4j 依赖（版本 0.35.0）
  - 添加 langchain4j-open-ai 依赖

#### 主界面
- `src/main/resources/com/junjie/githelper/main-view.fxml`
  - 在 TabPane 中添加 "AI 对话" 标签页

- `src/main/java/com/junjie/githelper/controller/MainViewController.java`
  - 添加 ChatViewController 引用
  - 实现 loadChatView() 方法
  - 实现 updateChatContext() 方法
  - 在切换到对话标签页时自动更新上下文（LLM 设置和当前项目）

#### 文档
- `README.md`
  - 在"核心功能"中添加对话功能说明
  - 添加"使用 AI 对话助手"使用指南
  - 在技术栈中添加 LangChain4j

### 🛠️ 技术栈

- **LangChain4j 0.35.0**
  - AI Services：将接口转换为 AI 驱动的服务
  - Tool 注解：标记可被 AI 调用的方法
  - Chat Memory：管理对话历史
  - OpenAI 兼容模型支持

### 📋 工具方法详情

#### 1. generateGitCommit
- **功能**：生成 Git commit 信息
- **参数**：`customInstructions`（可选）
- **返回**：暂存区变更摘要和提示

#### 2. generateCommitReport
- **功能**：生成指定时间段的提交报告
- **参数**：
  - `startDate`: 开始日期 (YYYY-MM-DD)
  - `endDate`: 结束日期 (YYYY-MM-DD)
  - `includeDetails`: 是否包含详细代码变更
- **返回**：提交日志和生成报告的提示

#### 3. checkStagedChanges
- **功能**：查看暂存区状态
- **参数**：无
- **返回**：暂存区的文件变更详情

#### 4. getRecentCommits
- **功能**：获取最近的提交历史
- **参数**：`count`（提交数量）
- **返回**：最近的提交记录列表

### 🎨 UI 设计

- **对话气泡样式**
  - 用户消息：蓝色背景，右对齐
  - 助手消息：灰色边框，左对齐
  - 系统消息：黄色背景，左对齐

- **输入区域**
  - 支持多行输入
  - Enter 发送，Shift+Enter 换行
  - 清空对话按钮

- **状态显示**
  - 顶部显示当前项目
  - 消息发送时显示"正在思考..."

### 🔐 安全性

- API Key 安全存储（使用 PasswordField）
- 支持代理设置
- 错误处理和用户友好的错误提示

### ⚠️ 已知限制

1. 工具方法目前返回的是分析结果，实际的 LLM 调用由 ChatAssistant 通过 LangChain4j 完成
2. 对话记忆限制为 20 条消息
3. 需要有效的 OpenAI 兼容 API 才能使用

### 🚀 使用前提

1. 配置 LLM 设置（API Key、Model、Base URL）
2. 选择一个 Git 项目
3. 确保网络连接正常（或配置代理）

### 📚 使用示例

```
用户：帮我生成一个 commit
助手：已分析代码变更...
      [显示暂存区变更摘要]

用户：生成本周的提交报告
助手：已获取本周提交记录...
      [显示提交统计和详情]

用户：查看暂存区状态
助手：当前暂存区变更：
      [显示文件列表和修改内容]
```

### 🎯 未来优化方向

1. 增强工具方法，直接在工具内完成 LLM 调用
2. 添加更多工具（git push、git pull、分支操作等）
3. 支持自定义工具
4. 支持导出对话历史
5. 优化 UI 交互体验

### 🤝 贡献者

本功能由 AI 助手协助实现，遵循 Commit-Pal 的代码规范和设计理念。

---

**版本**: v1.1.0-SNAPSHOT  
**更新日期**: 2025-11-04

