package com.yourcompany.githelper.service;

import com.yourcompany.githelper.model.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

    public void commit(Project project, String message) throws IOException, GitAPIException {
        File repoDir = new File(project.path());

        try (Repository repository = new FileRepositoryBuilder()
                .setGitDir(new File(repoDir, ".git"))
                .build();
             Git git = new Git(repository)) {

            git.commit().setMessage(message).call();
        }
    }
}
