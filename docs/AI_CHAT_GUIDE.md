# AI 对话助手使用指南

## 概述

Commit-Pal 的 AI 对话助手基于 [LangChain4j](https://github.com/langchain4j/langchain4j) 构建，能够理解自然语言并自动调用相应的工具来完成 Git 操作。

## 功能特性

### 🤖 智能意图识别

AI 助手能够理解你的自然语言输入，并自动识别你想要执行的操作：

- **生成 Commit 信息**
- **生成提交报告**
- **查看暂存区状态**
- **查看提交历史**

### 🔧 自动工具调用

助手会根据识别的意图，自动调用后台的工具函数（Tool），无需你手动操作界面。

### 💬 对话记忆

助手具有对话记忆功能（最多保留 20 条消息），可以进行多轮对话，理解上下文。

## 使用方法

### 1. 打开对话窗口

1. 确保已配置好 LLM 设置（API Key、模型等）
2. 选择一个 Git 项目
3. 切换到 **"AI 对话"** 标签页

### 2. 与助手对话

在输入框中输入你的请求，然后按 **Enter** 发送（Shift+Enter 换行）。

### 示例对话

#### 示例 1：生成 Commit 信息

```
你：帮我生成一个 commit 信息
助手：[分析暂存区变更并提供摘要]
你：生成一个简洁的中文提交信息
助手：[根据变更内容生成 commit 信息]
```

#### 示例 2：查看提交报告

```
你：生成本周的提交报告
助手：[自动获取本周的提交记录并生成报告]
```

#### 示例 3：查看状态

```
你：当前暂存区有什么变更？
助手：[显示暂存区文件列表和变更内容]
```

#### 示例 4：查看历史

```
你：最近有哪些提交？
助手：[显示最近的提交记录]
```

## 支持的工具

### 1. generateGitCommit

**功能**：生成 Git commit 信息

**触发关键词**：
- "生成 commit"
- "写个提交信息"
- "commit message"
- "帮我提交"

**参数**：
- `customInstructions`（可选）：自定义要求，如 "用中文"、"简洁一点" 等

### 2. generateCommitReport

**功能**：生成指定时间段的提交报告

**触发关键词**：
- "生成报告"
- "周报"
- "本周提交"
- "这周做了什么"

**参数**：
- `startDate`：开始日期（YYYY-MM-DD）
- `endDate`：结束日期（YYYY-MM-DD）
- `includeDetails`：是否包含详细代码变更

**示例**：
```
生成 2025-01-01 到 2025-01-07 的提交报告
```

### 3. checkStagedChanges

**功能**：查看暂存区状态

**触发关键词**：
- "暂存区"
- "有哪些改动"
- "staged changes"
- "查看变更"

### 4. getRecentCommits

**功能**：获取最近的提交历史

**触发关键词**：
- "最近的提交"
- "提交历史"
- "recent commits"
- "commit log"

**参数**：
- `count`：获取的提交数量（默认 5）

## 配置要求

### LLM 设置

确保在主界面右侧配置了以下信息：

1. **Provider**：LLM 提供商（如 OpenAI、Claude 等）
2. **API Key**：你的 API 密钥
3. **Model**：模型名称（如 gpt-4、gpt-3.5-turbo 等）
4. **Base URL**：API 基础 URL

### 代理设置（可选）

如果需要通过代理访问 LLM API：

1. 勾选 **"Enable Proxy"**
2. 填写 **Host** 和 **Port**
3. 保存设置

## 技术架构

```
用户输入
    ↓
ChatViewController
    ↓
ChatAssistant (LangChain4j AI Services)
    ↓
ChatLanguageModel (OpenAI 兼容)
    ↓
ToolService (@Tool 注解的方法)
    ↓
GitService / LLMService
    ↓
结果返回给用户
```

### 关键组件

1. **ChatAssistant**：定义了系统提示词和对话接口
2. **ToolService**：提供可被 AI 调用的工具方法（使用 `@Tool` 注解）
3. **ChatViewController**：管理 UI 和对话流程
4. **LangChain4j**：
   - `AiServices`：将接口转换为 AI 驱动的服务
   - `ChatMemory`：管理对话历史
   - `@Tool` 注解：标记可被 AI 调用的工具方法

## 常见问题

### Q1: 助手没有响应或报错

**A**: 请检查：
1. LLM API Key 是否正确
2. Base URL 是否正确
3. 网络连接是否正常
4. 如果使用代理，代理设置是否正确

### Q2: 助手理解不了我的意图

**A**: 尝试：
1. 使用更明确的关键词（参考上述"触发关键词"）
2. 将请求分解为更简单的步骤
3. 查看控制台日志了解具体错误

### Q3: 如何清空对话历史

**A**: 点击对话窗口底部的 **"清空对话"** 按钮。

### Q4: 能否支持其他 LLM 提供商

**A**: 可以！只要 LLM 提供商支持 OpenAI 兼容的 API 格式，就可以使用。只需修改 Base URL 即可。

## 未来计划

- [ ] 支持更多工具（如 git push、git pull 等）
- [ ] 支持自定义工具
- [ ] 支持语音输入
- [ ] 支持对话导出
- [ ] 支持多语言切换

## 反馈与建议

如有任何问题或建议，欢迎提交 Issue 或 Pull Request！

