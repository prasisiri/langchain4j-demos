package com.example.langchain4j.demos;

import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Collections;

public class Streaming {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("demo5")
                .load();

        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(dotenv.get("OPENAI_API_KEY"))
                .modelName("gpt-3.5-turbo")
                .temperature(0.7)
                .build();

        String prompt = "Write a short funny poem about developers and null-pointers, 10 lines maximum";

        System.out.println("Nr of chars: " + prompt.length());

        model.generate(Collections.singletonList(UserMessage.from(prompt)), new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                System.out.print(token);
            }

            public void onError(Throwable error) {
                System.err.println("Error: " + error.getMessage());
            }

            public void onComplete(AiMessage response) {
                System.out.println("\n\nDone streaming");
            }
        });
    }
}