package com.yourcompany.githelper.model;

public record LLMSettings(
    String provider,
    String api_key,
    String model,
    String base_url
) {}
