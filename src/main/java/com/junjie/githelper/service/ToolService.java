package com.junjie.githelper.service;

import com.junjie.githelper.model.Project;
import dev.langchain4j.agent.tool.Tool;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.time.LocalDate;

/**
 * 工具服务类，提供 AI 助手可以调用的工具方法
 */
public class ToolService {
    
    private final GitService gitService;
    private Project currentProject;
    
    public ToolService(GitService gitService, LLMService llmService) {
        this.gitService = gitService;
        // LLMService is passed for future use but not currently needed
        // as LLM calls are handled by the ChatAssistant via langchain4j
    }
    
    public void setCurrentProject(Project project) {
        this.currentProject = project;
    }
    
    /**
     * 生成 Git commit 信息
     * @param customInstructions 用户的自定义指令（可选）
     * @return 生成的 commit 信息
     */
    @Tool("生成 Git commit 信息。这个工具会分析当前暂存区的代码变更，并生成合适的 commit 消息。")
    public String generateGitCommit(String customInstructions) {
        if (currentProject == null) {
            return "❌ 错误：请先在左侧项目列表中选择一个 Git 项目。";
        }
        
        // 验证项目路径是否存在
        if (!new java.io.File(currentProject.path()).exists()) {
            return "❌ 错误：项目路径不存在：" + currentProject.path();
        }
        
        try {
            // 获取暂存区变更
            String stagedChanges = gitService.getStagedChanges(currentProject);
            if (stagedChanges.isEmpty()) {
                return "⚠️ 当前暂存区没有任何变更。\n\n" +
                       "请先使用以下命令添加文件到暂存区：\n" +
                       "  git add <文件名>  或  git add .\n\n" +
                       "项目路径：" + currentProject.path();
            }
            
            // 获取最近的提交历史作为参考
            String recentCommits = gitService.getRecentCommitMessages(currentProject);
            
            // 构建分析结果
            StringBuilder result = new StringBuilder();
            result.append("✅ 已分析项目：").append(currentProject.name()).append("\n\n");
            
            if (customInstructions != null && !customInstructions.trim().isEmpty()) {
                result.append("📝 用户要求：").append(customInstructions).append("\n\n");
            }
            
            // 返回暂存区变更摘要
            result.append("📊 暂存区变更摘要：\n");
            result.append("```\n");
            int maxLength = Math.min(800, stagedChanges.length());
            result.append(stagedChanges, 0, maxLength);
            if (stagedChanges.length() > 800) {
                result.append("\n...\n（变更内容较多，已截取前800字符）");
            }
            result.append("\n```\n\n");
            
            // 显示最近的提交记录作为参考
            if (recentCommits != null && !recentCommits.isEmpty()) {
                result.append("📜 最近的提交记录（供参考）：\n");
                result.append("```\n").append(recentCommits).append("\n```\n\n");
            }
            
            result.append("💡 提示：我现在可以基于这些变更为你生成 commit 信息。");
            result.append("请告诉我你希望生成什么样的提交信息（例如：\"生成一个简洁的中文 commit\"）。");
            
            return result.toString();
            
        } catch (IOException e) {
            return "❌ 读取 Git 仓库失败：" + e.getMessage() + "\n\n" +
                   "请确保：\n" +
                   "1. 项目路径是有效的 Git 仓库\n" +
                   "2. 项目路径：" + currentProject.path();
        } catch (GitAPIException e) {
            return "❌ Git 操作失败：" + e.getMessage() + "\n\n" +
                   "这可能是因为：\n" +
                   "1. 仓库损坏或不完整\n" +
                   "2. 没有访问权限\n" +
                   "项目路径：" + currentProject.path();
        }
    }
    
    /**
     * 生成指定时间段内的提交报告
     * @param startDate 开始日期（格式：YYYY-MM-DD）
     * @param endDate 结束日期（格式：YYYY-MM-DD）
     * @param includeDetails 是否包含详细的代码变更
     * @return 生成的提交报告
     */
    @Tool("生成指定时间段内的 Git 提交报告。这个工具会获取指定时间段内的所有提交记录，并生成一份总结报告。")
    public String generateCommitReport(String startDate, String endDate, boolean includeDetails) {
        if (currentProject == null) {
            return "❌ 错误：请先在左侧项目列表中选择一个 Git 项目。";
        }
        
        // 验证项目路径是否存在
        if (!new java.io.File(currentProject.path()).exists()) {
            return "❌ 错误：项目路径不存在：" + currentProject.path();
        }
        
        try {
            // 解析日期
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            if (start.isAfter(end)) {
                return "❌ 错误：开始日期（" + startDate + "）不能晚于结束日期（" + endDate + "）。";
            }
            
            // 获取提交日志
            String commitLogs = gitService.getCommitLogs(currentProject, start, end, includeDetails);
            
            if (commitLogs.startsWith("No commits")) {
                return "⚠️ " + commitLogs + "\n\n项目：" + currentProject.name();
            }
            
            return "✅ 已获取项目 **" + currentProject.name() + "** 的提交记录\n" +
                   "📅 时间范围：" + startDate + " 至 " + endDate + "\n\n" +
                   "📊 提交详情：\n" + commitLogs +
                   "\n\n💡 提示：我可以根据这些提交记录为你生成一份总结报告。" +
                   "请告诉我你需要什么样的报告（例如：\"生成一份工作周报\"）。";
            
        } catch (java.time.format.DateTimeParseException e) {
            return "❌ 日期格式错误：" + e.getMessage() + "\n\n" +
                   "正确格式：YYYY-MM-DD（例如：2025-01-01）";
        } catch (IOException e) {
            return "❌ 读取 Git 仓库失败：" + e.getMessage() + "\n项目路径：" + currentProject.path();
        } catch (GitAPIException e) {
            return "❌ Git 操作失败：" + e.getMessage() + "\n项目路径：" + currentProject.path();
        } catch (Exception e) {
            return "❌ 获取提交记录失败：" + e.getMessage();
        }
    }
    
    /**
     * 获取当前项目的暂存区状态
     * @return 暂存区状态描述
     */
    @Tool("查看当前 Git 项目暂存区的状态，了解有哪些文件被修改、添加或删除。")
    public String checkStagedChanges() {
        if (currentProject == null) {
            return "❌ 错误：请先在左侧项目列表中选择一个 Git 项目。";
        }
        
        // 验证项目路径是否存在
        if (!new java.io.File(currentProject.path()).exists()) {
            return "❌ 错误：项目路径不存在：" + currentProject.path();
        }
        
        try {
            String changes = gitService.getStagedChanges(currentProject);
            if (changes.isEmpty()) {
                return "⚠️ 当前暂存区没有任何变更。\n\n" +
                       "项目：" + currentProject.name() + "\n" +
                       "路径：" + currentProject.path() + "\n\n" +
                       "提示：使用 'git add <文件>' 将文件添加到暂存区。";
            }
            return "✅ 项目：" + currentProject.name() + "\n\n" +
                   "📊 暂存区变更：\n```\n" + changes + "\n```";
        } catch (IOException e) {
            return "❌ 读取 Git 仓库失败：" + e.getMessage() + "\n\n" +
                   "请确保项目路径是有效的 Git 仓库：\n" + currentProject.path();
        } catch (GitAPIException e) {
            return "❌ Git 操作失败：" + e.getMessage() + "\n项目路径：" + currentProject.path();
        } catch (Exception e) {
            return "❌ 获取暂存区状态失败：" + e.getMessage();
        }
    }
    
    /**
     * 获取项目的最近提交历史
     * @param count 获取的提交数量
     * @return 最近的提交历史
     */
    @Tool("查看项目的最近提交历史，了解最近都做了什么修改。")
    public String getRecentCommits(int count) {
        if (currentProject == null) {
            return "❌ 错误：请先在左侧项目列表中选择一个 Git 项目。";
        }
        
        // 验证项目路径是否存在
        if (!new java.io.File(currentProject.path()).exists()) {
            return "❌ 错误：项目路径不存在：" + currentProject.path();
        }
        
        try {
            String commits = gitService.getRecentCommitMessages(currentProject);
            if (commits == null || commits.trim().isEmpty()) {
                return "⚠️ 该项目还没有任何提交记录。\n\n" +
                       "项目：" + currentProject.name() + "\n" +
                       "路径：" + currentProject.path();
            }
            return "✅ 项目：" + currentProject.name() + "\n\n" +
                   "📜 最近 5 条提交记录：\n```\n" + commits + "\n```";
        } catch (IOException e) {
            return "❌ 读取 Git 仓库失败：" + e.getMessage() + "\n\n" +
                   "请确保项目路径是有效的 Git 仓库：\n" + currentProject.path();
        } catch (GitAPIException e) {
            return "❌ Git 操作失败：" + e.getMessage() + "\n项目路径：" + currentProject.path();
        } catch (Exception e) {
            return "❌ 获取提交历史失败：" + e.getMessage();
        }
    }
}

