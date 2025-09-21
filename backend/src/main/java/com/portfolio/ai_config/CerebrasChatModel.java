package com.portfolio.ai_config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class CerebrasChatModel implements ChatModel {

    private final WebClient webClient;
    private final CerebrasProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CerebrasChatModel(WebClient.Builder builder, CerebrasProperties properties) {
        this.webClient = builder.baseUrl(properties.getBaseUrl()).build();
        this.properties = properties;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        // Convert Spring AI messages -> Cerebras format
        List<Map<String, String>> messages = prompt.getUserMessages().stream()
                .map(m -> Map.of(
                        "role", getRole(m),
                        "content", m.getText()
                ))
                .toList();

        // Call Cerebras API
        String rawResponse = webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer "+properties.getApiKey())
                .bodyValue(Map.of(
                        "model", properties.getModel(),
                        "messages", messages
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            // Parse JSON
            JsonNode root = objectMapper.readTree(rawResponse);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            AssistantMessage assistantMessage = new AssistantMessage(content);
            // Wrap into Generation
            Generation generation = new Generation(assistantMessage);
            return new ChatResponse(List.of(generation));

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Cerebras response", e);
        }
    }

    private String getRole(Message message) {
        return switch (message.getMessageType()) {
            case USER -> "user";
            case SYSTEM -> "system";
            case ASSISTANT -> "assistant";
            default -> "user";
        };
    }
}
