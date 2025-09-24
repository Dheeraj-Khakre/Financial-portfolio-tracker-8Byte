package com.portfolio.ai_config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public ChatMemory getChatMemory(JdbcChatMemoryRepository repository){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }

    @Bean(name = "cerebras")
    public ChatClient getCerebrasChatClien(CerebrasChatModel cerebrasChatModel, ChatMemory chatMemory){
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return ChatClient.builder(cerebrasChatModel)
                .defaultAdvisors(messageChatMemoryAdvisor, SimpleLoggerAdvisor.builder().build())
                .defaultOptions(ChatOptions.builder()
                        .maxTokens(700)
                        .model("gpt-oss-120b")
                        .temperature(0.4)
                        .build())
                .build();
    }
}
