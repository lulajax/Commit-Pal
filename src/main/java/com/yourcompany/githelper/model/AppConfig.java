package com.yourcompany.githelper.model;

import java.util.List;

public record AppConfig(
    String version,
    LLMSettings llm_settings,
    List<Project> projects,
    String selected_project_id
) {}
