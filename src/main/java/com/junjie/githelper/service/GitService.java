package com.junjie.githelper.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.junjie.githelper.model.Project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class GitService {

    public String getStagedChanges(Project project) throws IOException, GitAPIException {
        File repoDir = new File(project.path());
        
        try (Repository repository = new FileRepositoryBuilder()
                .setGitDir(new File(repoDir, ".git"))
                .build();
             Git git = new Git(repository)) {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            git.diff()
               .setCached(true)
               .setOutputStream(outputStream)
               .call();
            
            return outputStream.toString();
        }
    }

    public String getRecentCommitMessages(Project project) throws IOException, GitAPIException {
        File repoDir = new File(project.path());

        try (Repository repository = new FileRepositoryBuilder()
            .setGitDir(new File(repoDir, ".git"))
            .build();
             Git git = new Git(repository)) {

            Iterable<RevCommit> commits = git.log().setMaxCount(5).call();
            return StreamSupport.stream(commits.spliterator(), false)
                .map(RevCommit::getShortMessage)
                .collect(Collectors.joining("\n"));
        }
    }

    public void commit(Project project, String message) throws IOException, GitAPIException {
        File repoDir = new File(project.path());

        try (Repository repository = new FileRepositoryBuilder()
                .setGitDir(new File(repoDir, ".git"))
                .build();
             Git git = new Git(repository)) {

            git.commit().setMessage(message).call();
        }
    }
    
    /**
     * Gets the commit logs for a specified period (including code changes).
     * @param project The project.
     * @param startDate The start date.
     * @param endDate The end date.
     * @param includeDiff Whether to include code change details.
     * @return Formatted commit logs.
     */
    public String getCommitLogs(Project project, LocalDate startDate, LocalDate endDate, boolean includeDiff) throws IOException, GitAPIException {
        File repoDir = new File(project.path());
        
        // Convert LocalDate to Date
        Date since = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date until = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        try (Repository repository = new FileRepositoryBuilder()
                .setGitDir(new File(repoDir, ".git"))
                .build();
             Git git = new Git(repository)) {
            
            Iterable<RevCommit> commits = git.log().call();
            StringBuilder logBuilder = new StringBuilder();
            int count = 0;
            
            for (RevCommit commit : commits) {
                Date commitDate = new Date(commit.getCommitTime() * 1000L);
                
                // Check if the commit is within the specified time range
                if (commitDate.after(since) && commitDate.before(until)) {
                    count++;
                    Instant instant = Instant.ofEpochSecond(commit.getCommitTime());
                    LocalDate commitLocalDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                    
                    logBuilder.append("=".repeat(80)).append("\n");
                    logBuilder.append("Commit: ").append(commit.getName().substring(0, 8)).append("\n");
                    logBuilder.append("Author: ").append(commit.getAuthorIdent().getName()).append("\n");
                    logBuilder.append("Date: ").append(commitLocalDate).append("\n");
                    logBuilder.append("Message: ").append(commit.getFullMessage()).append("\n");
                    
                    // If diff is needed
                    if (includeDiff) {
                        try {
                            ByteArrayOutputStream diffStream = new ByteArrayOutputStream();
                            
                            // Get parent commit
                            if (commit.getParentCount() > 0) {
                                RevCommit parent = commit.getParent(0);
                                git.diff()
                                   .setOldTree(prepareTreeParser(repository, parent))
                                   .setNewTree(prepareTreeParser(repository, commit))
                                   .setOutputStream(diffStream)
                                   .call();
                                
                                String diff = diffStream.toString();
                                if (!diff.isEmpty()) {
                                    logBuilder.append("\nCode Changes:\n");
                                    logBuilder.append(diff);
                                }
                            } else {
                                // Initial commit, show all files
                                logBuilder.append("\n[Initial commit - showing all new files]\n");
                            }
                        } catch (Exception e) {
                            logBuilder.append("\n[Could not get code changes for this commit: ").append(e.getMessage()).append("]\n");
                        }
                    }
                    
                    logBuilder.append("\n");
                }
            }
            
            if (count == 0) {
                return "No commits found between " + startDate + " and " + endDate + ".";
            }
            
            String header = String.format("Found %d commits (%s to %s)\n\n", count, startDate, endDate);
            return header + logBuilder.toString();
        }
    }
    
    /**
     * Legacy method compatibility - does not include diff
     */
    public String getCommitLogs(Project project, LocalDate startDate, LocalDate endDate) throws IOException, GitAPIException {
        return getCommitLogs(project, startDate, endDate, false);
    }
    
    /**
     * Prepare TreeParser for diff
     */
    private org.eclipse.jgit.treewalk.AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
        try (org.eclipse.jgit.revwalk.RevWalk walk = new org.eclipse.jgit.revwalk.RevWalk(repository)) {
            RevCommit parsedCommit = walk.parseCommit(commit.getId());
            org.eclipse.jgit.treewalk.CanonicalTreeParser treeParser = new org.eclipse.jgit.treewalk.CanonicalTreeParser();
            try (org.eclipse.jgit.lib.ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, parsedCommit.getTree().getId());
            }
            return treeParser;
        }
    }
}
