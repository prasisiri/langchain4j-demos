package com.example.langchain4j.demos;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;
import static java.nio.file.Paths.get;

public class EasyRagExample {

        private static final Dotenv dotenv = Dotenv.configure()
                        .directory("demo12-rag01")
                        .load();

        public static void main(String[] args) {
                // Load document
                Document document = loadDocument(
                                get("demo12-rag01/src/main/resources/documents/sample.txt"),
                                new TextDocumentParser());

                // Split document into segments
                List<TextSegment> segments = recursive(300, 0).split(document);

                // Create embedding model and generate embeddings
                EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
                var embeddings = embeddingModel.embedAll(segments).content();

                // Store embeddings
                EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
                embeddingStore.addAll(embeddings, segments);

                // Define question and create its embedding
                String question = "When can I cancel my reservation?";
                var questionEmbedding = embeddingModel.embed(question).content();

                // Find relevant embeddings
                var relevantEmbeddings = embeddingStore.findRelevant(
                                questionEmbedding, 3, 0.6);

                // Create prompt template
                PromptTemplate promptTemplate = PromptTemplate.from(
                                "Answer the question based on the following information:\n\n" +
                                                "Information:\n{{information}}\n\n" +
                                                "Question:\n{{question}}");

                // Prepare information from relevant embeddings
                String information = relevantEmbeddings.stream()
                                .map(match -> match.embedded().text())
                                .collect(StringBuilder::new,
                                                (sb, text) -> sb.append(text).append("\n\n"),
                                                StringBuilder::append)
                                .toString();

                // Create prompt variables
                Map<String, Object> variables = new HashMap<>();
                variables.put("question", question);
                variables.put("information", information);

                // Generate final prompt
                Prompt prompt = promptTemplate.apply(variables);

                // Create chat model and get response
                ChatLanguageModel chatModel = OpenAiChatModel.builder()
                                .apiKey(dotenv.get("OPENAI_API_KEY"))
                                .modelName("gpt-3.5-turbo")
                                .build();

                // Get and print response
                var response = chatModel.generate(List.of(prompt.toUserMessage()));
                System.out.println("Question: " + question);
                System.out.println("Answer: " + response.content().text());
        }
}