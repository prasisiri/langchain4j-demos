package com.example.langchain4j.demos;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import io.github.cdimascio.dotenv.Dotenv;

public class Memory {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("demo6")
                .load();

        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(dotenv.get("OPENAI_API_KEY"))
                .modelName("gpt-3.5-turbo")
                .temperature(0.7)
                .build();

        // Create chat memory with tokenizer
        ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(1000, new OpenAiTokenizer());

        // First interaction
        UserMessage userMessage1 = UserMessage.from("Hi! My name is Klaus.");
        chatMemory.add(userMessage1);

        System.out.println("[User]: " + userMessage1.text());

        AiMessage aiMessage1 = model.generate(chatMemory.messages()).content();
        chatMemory.add(aiMessage1);

        System.out.println("[Assistant]: " + aiMessage1.text());

        // Second interaction
        UserMessage userMessage2 = UserMessage.from("What's my name?");
        chatMemory.add(userMessage2);

        System.out.println("\n\n[User]: " + userMessage2.text());

        AiMessage aiMessage2 = model.generate(chatMemory.messages()).content();
        chatMemory.add(aiMessage2);

        System.out.println("[Assistant]: " + aiMessage2.text());
    }
}