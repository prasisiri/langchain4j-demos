package com.example.langchain4j.demos;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;

public class PromptTemplateExamples {

    static class Simple_Prompt_Template_Example {

        public static void main(String[] args) {
            // Load environment variables from .env file
            Dotenv dotenv = Dotenv.configure()
                    .directory("demo4")
                    .load();

            ChatLanguageModel model = OpenAiChatModel.builder()
                    .apiKey(dotenv.get("OPENAI_API_KEY"))
                    .modelName("gpt-3.5-turbo")
                    .timeout(ofSeconds(60))
                    .build();

            String template = "Create a recipe for a {{dishType}} with the following ingredients: {{ingredients}}";
            PromptTemplate promptTemplate = PromptTemplate.from(template);

            Map<String, Object> variables = new HashMap<>();
            variables.put("dishType", "oven dish");
            variables.put("ingredients", "potato, tomato, feta, olive oil");

            Prompt prompt = promptTemplate.apply(variables);

            String response = model.generate(prompt.text());

            System.out.println(response);
        }
    }

    static class Structured_Prompt_Template_Example {
        @StructuredPrompt({
                "Create a recipe of a {{dish}} that can be prepared using only {{ingredients}}.",
                "Structure your answer in the following way:",

                "Recipe name: ...",
                "Description: ...",
                "Preparation time: ...",

                "Required ingredients:",
                "- ...",
                "- ...",

                "Instructions:",
                "- ...",
                "- ..."
        })
        static class CreateRecipePrompt {
            String dish;
            List<String> ingredients;

            CreateRecipePrompt(String dish, List<String> ingredients) {
                this.dish = dish;
                this.ingredients = ingredients;
            }
        }

        public static void main(String[] args) {
            // Load environment variables from .env file
            Dotenv dotenv = Dotenv.configure()
                    .directory("demo4")
                    .load();

            ChatLanguageModel model = OpenAiChatModel.builder()
                    .apiKey(dotenv.get("OPENAI_API_KEY"))
                    .modelName("gpt-3.5-turbo")
                    .timeout(ofSeconds(60))
                    .build();

            CreateRecipePrompt createRecipePrompt = new CreateRecipePrompt(
                    "salad",
                    asList("cucumber", "tomato", "feta", "onion", "olives"));

            Prompt prompt = StructuredPromptProcessor.toPrompt(createRecipePrompt);

            String recipe = model.generate(prompt.text());

            System.out.println(recipe);
        }
    }
}