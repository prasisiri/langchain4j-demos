package com.example.langchain4j.demos;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
class AssistantTools {

    /**
     * This tool is available to {@link Assistant}
     */
    @Tool
    String currentTime() {
        return LocalTime.now().toString();
    }
}