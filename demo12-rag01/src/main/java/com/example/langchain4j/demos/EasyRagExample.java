package com.example.langchain4j.demos;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.github.cdimascio.dotenv.Dotenv;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;
import static java.nio.file.Paths.get;

public class EasyRagExample {

        private static final Dotenv dotenv = Dotenv.configure()
                        .directory("demo12-rag01")
                        .load();

        private static final ChatLanguageModel CHAT_MODEL = GitHubModelsChatModel.builder()
                        .gitHubToken(dotenv.get("GITHUB_TOKEN"))
                        .modelName("gpt-4o-mini")
                        .temperature(0.7)
                        .build();

        interface Assistant {
                String chat(String message);
        }

        public static void main(String[] args) {
                // Load documents for RAG
                // First, let's load documents that we want to use for RAG
                List<Document> documents = loadDocuments(
                                get("demo12-rag01/src/main/resources/documents"),
                                FileSystems.getDefault().getPathMatcher("glob:*.txt"));

                // Create assistant with RAG capabilities
                Assistant assistant = AiServices.builder(Assistant.class)
                                .chatLanguageModel(CHAT_MODEL)
                                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                                .contentRetriever(createContentRetriever(documents))
                                .build();

                // Test the assistant
                System.out.println("Assistant: " + assistant.chat("What documents are available?"));
        }

        private static ContentRetriever createContentRetriever(List<Document> documents) {
                // Create embedding store
                EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

                // Ingest documents
                EmbeddingStoreIngestor.ingest(documents, embeddingStore);

                // Create and return content retriever
                return EmbeddingStoreContentRetriever.from(embeddingStore);
        }
}