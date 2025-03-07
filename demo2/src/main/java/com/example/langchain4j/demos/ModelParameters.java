package com.example.langchain4j.demos;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.github.cdimascio.dotenv.Dotenv;

public class ModelParameters {
    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("demo2")
                .load();

        // Initialize OpenAI chat model with parameters
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(dotenv.get("OPENAI_API_KEY"))
                .modelName("gpt-3.5-turbo")
                .temperature(0.7)
                .maxTokens(100)
                // .logRequests(true)
                // .logResponses(true)
                .build();

        // Test the model
        String prompt = "Which is better langchain4j or springAi";
        String response = model.generate(prompt); // Changed from chat() to generate()
        System.out.println("AI Response: " + response);
    }
}