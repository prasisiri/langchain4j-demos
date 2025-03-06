package com.example.langchain4j.demos;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.github.GitHubModelsChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import io.github.cdimascio.dotenv.Dotenv;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static java.nio.file.Paths.get;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.agent.tool.Tool;

public class AdvancedRagSqlExample {

    private static final Dotenv dotenv = Dotenv.configure()
            .directory("demo12-rag02")
            .load();

    private static final ChatLanguageModel CHAT_MODEL = GitHubModelsChatModel.builder()
            .gitHubToken(dotenv.get("GITHUB_TOKEN"))
            .modelName("gpt-4o-mini")
            .temperature(0.7)
            .build();

    @SystemMessage({
            "You are a helpful assistant with access to a SQL database containing customer, product, and order information.",
            "Use the provided tools to query the database and answer questions about the data."
    })
    interface Assistant {
        String chat(String message);
    }

    static class DatabaseTools {
        private final DataSource dataSource;

        DatabaseTools(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Tool("Query the number of customers")
        int getCustomerCount() {
            try (Connection conn = dataSource.getConnection();
                    Statement stmt = conn.createStatement();
                    var rs = stmt.executeQuery("SELECT COUNT(*) FROM customers")) {
                rs.next();
                return rs.getInt(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Assistant assistant = createAssistant();
        System.out.println("Assistant: " + assistant.chat("How many customers do we have?"));
    }

    private static Assistant createAssistant() {
        DataSource dataSource = createDataSource();

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(CHAT_MODEL)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(new DatabaseTools(dataSource))
                .build();
    }

    private static DataSource createDataSource() {
        try {
            // Start H2 Console first
            Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();

            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"); // Simplified URL
            dataSource.setUser("sa");
            dataSource.setPassword("sa");

            String createTablesScript = read("sql/create_tables.sql");
            execute(createTablesScript, dataSource);

            String prefillTablesScript = read("sql/prefill_tables.sql");
            execute(prefillTablesScript, dataSource);

            return dataSource;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create or start H2 database", e);
        }
    }

    private static String read(String path) {
        try {
            return new String(Files.readAllBytes(get("demo12-rag02/src/main/resources/" + path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void execute(String sql, DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            for (String sqlStatement : sql.split(";")) {
                statement.execute(sqlStatement.trim());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}