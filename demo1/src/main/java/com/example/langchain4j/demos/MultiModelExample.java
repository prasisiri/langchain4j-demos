package com.example.langchain4j.demos;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import dev.langchain4j.model.github.GitHubModelsChatModelName;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import io.github.cdimascio.dotenv.Dotenv;

public class MultiModelExample {

    private static final Dotenv dotenv = Dotenv.configure()
            .directory("demo1")
            .load();

    public static void main(String[] args) {
        // Initialize OpenAI model
        ChatLanguageModel openAiModel = OpenAiChatModel.builder()
                .apiKey(dotenv.get("OPENAI_API_KEY"))
                .modelName("gpt-3.5-turbo")
                .build();

        // Initialize GitHub model with Claude
        ChatLanguageModel claudeModel = GitHubModelsChatModel.builder()
                .gitHubToken(dotenv.get("GITHUB_TOKEN"))
                .modelName("Mistral-small")
                .temperature(0.7)
                .build();

        // Test both models with same prompt
        String prompt = "What is the langchain4j project?";
        System.out.println("OpenAI Response:");
        System.out.println(openAiModel.generate(prompt));
        System.out.println("\nClaude Response:");
        System.out.println(claudeModel.generate(prompt));
    }
}