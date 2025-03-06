package com.example.langchain4j.demos;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
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

        // Regular chat
        String regularResponse = model.generate("Write a cover letter for a Java developer job using AI!");
        System.out.println("Regular Response:\n" + regularResponse);

        // Pirate chat
        var response = model.generate(
                SystemMessage.from("Answer like you are a pirate."),
                UserMessage.from("Write a cover letter for a Java developer job using AI."));

        System.out.println("\nPirate Response:\n" + response.content().text());
    }
}