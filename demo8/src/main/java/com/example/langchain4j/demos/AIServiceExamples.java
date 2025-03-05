package com.example.langchain4j.demos;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;

public class AIServiceExamples {

    static ChatLanguageModel model;

    static {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("demo8")
                .load();

        model = OpenAiChatModel.builder()
                .apiKey(dotenv.get("OPENAI_API_KEY"))
                .modelName("gpt-3.5-turbo")
                .timeout(ofSeconds(60))
                .build();
    }

    // Simple example demonstrating basic chat functionality
    static class SimpleExample {
        interface Assistant {
            String chat(String message);
        }

        public static void main(String[] args) {
            Assistant assistant = AiServices.create(Assistant.class, model);
            String response = assistant.chat("Translate 'Hello world' to French");
            System.out.println(response);
        }
    }

    // Example showing sentiment analysis capabilities
    static class SentimentExample {
        enum Sentiment {
            POSITIVE, NEUTRAL, NEGATIVE
        }

        interface SentimentAnalyzer {
            @UserMessage("Analyze sentiment of {{it}}")
            Sentiment analyzeSentimentOf(String text);
        }

        public static void main(String[] args) {
            SentimentAnalyzer analyzer = AiServices.create(SentimentAnalyzer.class, model);
            Sentiment sentiment = analyzer.analyzeSentimentOf("I love this product!");
            System.out.println(sentiment); // POSITIVE
        }
    }

    // Example demonstrating date and time extraction
    static class DateTimeExample {
        interface DateTimeExtractor {
            @UserMessage("Extract date from {{it}}")
            LocalDate extractDateFrom(String text);

            @UserMessage("Extract time from {{it}}")
            LocalTime extractTimeFrom(String text);
        }

        public static void main(String[] args) {
            DateTimeExtractor extractor = AiServices.create(DateTimeExtractor.class, model);
            String text = "The meeting is scheduled for July 21, 2024 at 2:30 PM";

            LocalDate date = extractor.extractDateFrom(text);
            LocalTime time = extractor.extractTimeFrom(text);

            System.out.println("Date: " + date);
            System.out.println("Time: " + time);
        }
    }

    // Example showing chat memory functionality
    static class MemoryExample {
        interface Assistant {
            String chat(String message);
        }

        public static void main(String[] args) {
            ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(4);

            Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(model)
                    .chatMemory(chatMemory)
                    .build();

            System.out.println(assistant.chat("Hi! My name is Alice."));
            System.out.println(assistant.chat("What's my name?")); // Should remember "Alice"
        }
    }

    // Example demonstrating structured data extraction
    static class StructuredDataExample {
        static class Person {
            @Description("first name of the person")
            private String firstName;

            @Description("last name of the person")
            private String lastName;

            private LocalDate birthDate;

            @Override
            public String toString() {
                return String.format("Person{firstName='%s', lastName='%s', birthDate=%s}",
                        firstName, lastName, birthDate);
            }
        }

        interface PersonExtractor {
            @UserMessage("Extract person details from: {{it}}")
            Person extractPerson(String text);
        }

        public static void main(String[] args) {
            PersonExtractor extractor = AiServices.create(PersonExtractor.class, model);

            String text = "John Smith was born on May 15, 1990";
            Person person = extractor.extractPerson(text);

            System.out.println(person);
        }
    }
}