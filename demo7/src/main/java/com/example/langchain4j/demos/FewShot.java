package com.example.langchain4j.demos;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.AiMessage;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

public class FewShot {
        public static void main(String[] args) {
                // Load environment variables
                Dotenv dotenv = Dotenv.configure()
                                .directory("demo7")
                                .load();

                // Initialize model
                ChatLanguageModel model = OpenAiChatModel.builder()
                                .apiKey(dotenv.get("OPENAI_API_KEY"))
                                .modelName("gpt-3.5-turbo")
                                .build();

                // Create few-shot examples
                List<ChatMessage> messages = new ArrayList<>();

                // Example 1
                messages.add(UserMessage.from("India"));
                messages.add(AiMessage.from("aidnI"));

                // Example 2
                messages.add(UserMessage.from("Canada"));
                messages.add(AiMessage.from("adanaC"));

                // Example 3
                messages.add(UserMessage.from("Australia"));
                messages.add(AiMessage.from("ailartsA"));

                // Add the actual question
                messages.add(UserMessage.from("Brazil"));

                // Get response
                var response = model.generate(messages);

                // Print all messages including response
                System.out.println("Few-shot learning demonstration:");
                System.out.println("--------------------------------");
                for (int i = 0; i < messages.size(); i += 2) {
                        System.out.println("Human: " + messages.get(i).text());
                        if (i + 1 < messages.size()) {
                                System.out.println("AI: " + messages.get(i + 1).text());
                        }
                }
                System.out.println("AI: " + response.content().text());
        }
}