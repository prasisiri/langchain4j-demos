package com.example.langchain4j.demos;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.github.cdimascio.dotenv.Dotenv;

public class HelloWorld {
    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("demo1") // specify the directory containing .env
                .load();

        // Initialize OpenAI chat model
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(dotenv.get("OPENAI_API_KEY"))
                .modelName("gpt-3.5-turbo")
                .build();

        // Send a simple message
        String response = model.generate("Say hello world!");
        System.out.println("AI Response: " + response);
    }
}