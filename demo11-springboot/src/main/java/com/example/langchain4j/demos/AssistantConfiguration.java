package com.example.langchain4j.demos;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AssistantConfiguration {

    @Value("${langchain4j.github.token}")
    private String githubToken;

    @Value("${langchain4j.github.temperature}")
    private double temperature;

    @Value("${langchain4j.github.model}")
    private String model;

    @Bean
    ChatLanguageModel chatLanguageModel() {
        return GitHubModelsChatModel.builder()
                .gitHubToken(githubToken)
                .modelName(model)
                .temperature(temperature)
                .build();
    }
}