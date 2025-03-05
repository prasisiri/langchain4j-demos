package com.example.langchain4j.demos;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import io.github.cdimascio.dotenv.Dotenv;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_4;

public class ServiceWithToolsExample {

    static class Calculator {

        @Tool("Calculates the length of a string")
        int stringLength(String s) {
            System.out.println("Called stringLength() with s='" + s + "'");
            return s.length();
        }

        @Tool("Calculates the sum of two numbers")
        int add(int a, int b) {
            System.out.println("Called add() with a=" + a + ", b=" + b);
            return a + b;
        }

        @Tool("Calculates the square root of a number")
        double sqrt(int x) {
            System.out.println("Called sqrt() with x=" + x);
            return Math.sqrt(x);
        }
    }

    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("demo10")
                .load();

        ChatLanguageModel model = GitHubModelsChatModel.builder()
                .gitHubToken(dotenv.get("GITHUB_TOKEN"))
                .modelName("gpt-4o-mini")
                .temperature(0.7)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .tools(new Calculator())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String question = "What is the square root of the sum of the numbers of letters in the words \"hello\" and \"world\"?";

        String answer = assistant.chat(question);

        System.out.println(answer);
    }
}