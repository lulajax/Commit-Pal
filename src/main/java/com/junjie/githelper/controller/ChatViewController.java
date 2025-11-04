package com.junjie.githelper.controller;

import com.junjie.githelper.model.LLMSettings;
import com.junjie.githelper.model.Project;
import com.junjie.githelper.service.ChatAssistant;
import com.junjie.githelper.service.GitService;
import com.junjie.githelper.service.LLMService;
import com.junjie.githelper.service.ToolService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 对话窗口控制器
 */
public class ChatViewController {
    
    @FXML private VBox chatContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextArea userInputTextArea;
    @FXML private Button sendButton;
    @FXML private Button clearButton;
    @FXML private Label statusLabel;
    
    private ChatAssistant chatAssistant;
    private ToolService toolService;
    private LLMSettings llmSettings;
    private Project currentProject;
    private GitService gitService;
    private LLMService llmService;
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public void initialize() {
        // 初始化服务
        gitService = new GitService();
        llmService = new LLMService();
        toolService = new ToolService(gitService, llmService);
        
        // 绑定按钮事件
        sendButton.setOnAction(event -> onSendMessage());
        clearButton.setOnAction(event -> onClearChat());
        
        // 设置 Enter 发送，Shift+Enter 换行
        userInputTextArea.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                if (event.isShiftDown()) {
                    // Shift+Enter 换行（默认行为）
                } else {
                    // Enter 发送
                    event.consume();
                    onSendMessage();
                }
            }
        });
        
        // 自动滚动到底部
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            chatScrollPane.setVvalue(1.0);
        });
        
        // 添加欢迎消息
        addAssistantMessage("你好！我是 Commit-Pal 智能助手。\n\n" +
                "我可以帮你：\n" +
                "• 生成 Git commit 信息\n" +
                "• 生成指定时间段的提交报告\n" +
                "• 查看暂存区状态\n" +
                "• 查看提交历史\n\n" +
                "请告诉我你需要什么帮助！");
    }
    
    /**
     * 设置 LLM 设置和当前项目
     */
    public void setContext(LLMSettings settings, Project project) {
        this.llmSettings = settings;
        this.currentProject = project;
        this.toolService.setCurrentProject(project);
        
        // 初始化 ChatAssistant
        initializeChatAssistant();
        
        // 更新状态
        if (project != null) {
            statusLabel.setText("当前项目: " + project.name());
        } else {
            statusLabel.setText("未选择项目");
        }
    }
    
    /**
     * 初始化 ChatAssistant
     */
    private void initializeChatAssistant() {
        if (llmSettings == null) {
            return;
        }
        
        try {
            // 构建 OpenAI 兼容的 ChatLanguageModel
            OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                    .apiKey(llmSettings.api_key())
                    .modelName(llmSettings.model())
                    .baseUrl(llmSettings.base_url())
                    .timeout(java.time.Duration.ofSeconds(60));
            
            // 如果启用了代理
            if (Boolean.TRUE.equals(llmSettings.use_proxy()) && 
                llmSettings.proxy_host() != null && 
                llmSettings.proxy_port() != null) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, 
                        new InetSocketAddress(llmSettings.proxy_host(), llmSettings.proxy_port()));
                builder.proxy(proxy);
            }
            
            ChatLanguageModel chatModel = builder.build();
            
            // 创建 AI Services
            chatAssistant = AiServices.builder(ChatAssistant.class)
                    .chatLanguageModel(chatModel)
                    .tools(toolService)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                    .build();
                    
        } catch (Exception e) {
            e.printStackTrace();
            addSystemMessage("初始化 AI 助手失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送消息
     */
    private void onSendMessage() {
        String userMessage = userInputTextArea.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }
        
        if (chatAssistant == null) {
            addSystemMessage("请先配置 LLM 设置并选择项目。");
            return;
        }
        
        // 显示用户消息
        addUserMessage(userMessage);
        userInputTextArea.clear();
        
        // 禁用发送按钮
        sendButton.setDisable(true);
        statusLabel.setText("正在思考...");
        
        // 在后台线程调用 AI
        new Thread(() -> {
            try {
                String response = chatAssistant.chat(userMessage);
                Platform.runLater(() -> {
                    addAssistantMessage(response);
                    statusLabel.setText(currentProject != null ? "当前项目: " + currentProject.name() : "就绪");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    addSystemMessage("错误: " + e.getMessage());
                    statusLabel.setText("发生错误");
                });
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> sendButton.setDisable(false));
            }
        }).start();
    }
    
    /**
     * 清空对话
     */
    private void onClearChat() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认");
        confirmAlert.setHeaderText("清空对话历史");
        confirmAlert.setContentText("确定要清空所有对话记录吗？");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                chatContainer.getChildren().clear();
                // 重新初始化 ChatAssistant 以清空记忆
                initializeChatAssistant();
                addAssistantMessage("对话历史已清空。有什么我可以帮你的吗？");
            }
        });
    }
    
    /**
     * 添加用户消息到聊天界面
     */
    private void addUserMessage(String message) {
        addMessage(message, "user-message", "你", true);
    }
    
    /**
     * 添加助手消息到聊天界面
     */
    private void addAssistantMessage(String message) {
        addMessage(message, "assistant-message", "助手", false);
    }
    
    /**
     * 添加系统消息到聊天界面
     */
    private void addSystemMessage(String message) {
        addMessage(message, "system-message", "系统", false);
    }
    
    /**
     * 添加消息到聊天界面
     */
    private void addMessage(String message, String styleClass, String sender, boolean alignRight) {
        VBox messageBox = new VBox(5);
        messageBox.setPadding(new Insets(10));
        messageBox.getStyleClass().add("message-box");
        
        // 发送者和时间标签
        Label headerLabel = new Label(sender + " - " + LocalDateTime.now().format(TIME_FORMATTER));
        headerLabel.getStyleClass().add("message-header");
        
        // 消息内容
        TextFlow messageContent = new TextFlow();
        Text text = new Text(message);
        text.getStyleClass().add("message-text");
        messageContent.getChildren().add(text);
        messageContent.getStyleClass().add(styleClass);
        messageContent.setPadding(new Insets(10));
        
        messageBox.getChildren().addAll(headerLabel, messageContent);
        
        // 对齐方式
        if (alignRight) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            HBox.setHgrow(messageBox, Priority.NEVER);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }
        
        chatContainer.getChildren().add(messageBox);
        
        // 自动滚动到底部
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }
}

