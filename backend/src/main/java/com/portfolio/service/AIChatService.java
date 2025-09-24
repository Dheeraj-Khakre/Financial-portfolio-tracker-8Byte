package com.portfolio.service;

import com.portfolio.dto.AIInsightResponse;
import com.portfolio.dto.SymbolSuggest;
import com.portfolio.entity.Asset;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.User;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIChatService {

    private final ChatClient chatClient;
    private final UserRepository userRepository;

    /**
     * Generates a concise, professional financial insight for the user's portfolio.
     */
    public AIInsightResponse getAiInsightResponse(Portfolio portfolio, Long userId) {
        String userPrompt = buildPortfolioPrompt(portfolio, userId);

        return chatClient
                .prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, userId))
                .system("You are a professional financial analyst and stock-market strategist. "
                        + "Always respond with clear, summarized insights.")
                .user(userPrompt)
                .call()
                .entity(AIInsightResponse.class);
    }

    /**
     * Builds a summarized portfolio description for the AI model.
     */
    private String buildPortfolioPrompt(Portfolio portfolio, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        StringBuilder sb = new StringBuilder();
        sb.append("Analyze the following investment portfolio for user ")
                .append(user.getUsername())
                .append(". Provide a concise market insight, risk assessment, and actionable suggestions.\n\n")
                .append("Portfolio Name: ").append(portfolio.getName()).append("\n")
                .append("Description: ").append(portfolio.getDescription()).append("\n")
                .append("Total Value: ").append(portfolio.getTotalValue()).append("\n\n")
                .append("Assets:\n");

        for (Asset a : portfolio.getAssets()) {
            sb.append("- ").append(a.getTickerSymbol())
                    .append(" (").append(a.getCompanyName()).append(") ")
                    .append("| Qty: ").append(a.getQuantity())
                    .append(" | Purchase: ").append(a.getPurchasePrice())
                    .append(" | Current: ").append(a.getCurrentPrice())
                    .append(" | Total: ").append(a.getTotalValue())
                    .append("\n");
        }

        sb.append("\nFocus on performance trends, sector diversification, and key opportunities or risks "
                + "based on current market conditions. Respond with a concise summary.");
        return sb.toString();
    }

    /**
     * Answers a free-form user question with a summarized stock-market analysis.
     */
    public String getResponse(String question, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return chatClient
                .prompt(question)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, userId))
                .system(
                        "You are an expert in stock analysis and market trends. "
                                + "Provide clear, concise financial insights. "
                                + "Act as a professional financial data assistant."
                )
                .user(
                        "Answer only questions related to finance, investments, or the stock market. and current user related and portfolio related  "
                                + "If the question is not finance-related, politely just give normal to respond."
                )
                .call()
                .content();
    }

    /**
     * Finds the closest U.S. stock symbol for a given free-text input.
     */
    public SymbolSuggest findClosestStockSymbol(String userInput, Long userId) {
        String userPrompt = """
            A user entered the text: "%s".
            Return only the single most likely U.S. stock ticker symbol and company name.
            If no close match exists, respond with "NONE".
            Do not explain—just return the symbol and company name.
            Treat the returned symbol as the most recent asset/stock selection.
            """.formatted(userInput);

        return chatClient
                .prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, userId))
                .system("You are a financial data assistant specializing in matching free-text queries "
                        + "to U.S. stock symbols. Provide only a concise answer.")
                .user(userPrompt)
                .call()
                .entity(SymbolSuggest.class);
    }
}
