package com.example.langchain4j.demos;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import io.github.cdimascio.dotenv.Dotenv;

public class OpenAiImageModelExamples {

        public static void main(String[] args) {
                // Load environment variables from .env file
                Dotenv dotenv = Dotenv.configure()
                                .directory("demo3")
                                .load();

                OpenAiImageModel model = OpenAiImageModel.builder()
                                .apiKey(dotenv.get("OPENAI_API_KEY"))
                                .modelName("gpt-3.5-turbo")
                                .size("1024x1024") // Specify image size
                                .build();

                Response<Image> response = model.generate(
                                "Swiss software developers with cheese fondue, a parrot and a cup of coffee");

                System.out.println("Generated Image URL: " + response.content().url());
        }
}