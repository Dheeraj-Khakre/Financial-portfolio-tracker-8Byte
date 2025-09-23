package com.portfolio.service;

import com.portfolio.dto.AIInsightResponse;
import com.portfolio.dto.SymbolSuggest;
import com.portfolio.entity.Asset;
import com.portfolio.entity.Portfolio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AIChatService {

    private  final ChatClient chatClient;

    public AIChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public AIInsightResponse getAiInsightResponse(Portfolio portfolio, String userName) {
        String userPrompt = buildPortfolioPrompt(portfolio, userName);

        return chatClient
                .prompt()
                .system("You are a professional financial analyst and stock-market strategist.")
                .user(userPrompt)
                .call()
                .entity(AIInsightResponse.class);
    }
    private String buildPortfolioPrompt(Portfolio portfolio, String userName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze this investment portfolio for ").append(userName).append(". ")
                .append("Provide a concise market insight, risk assessment, and suggestions.\n\n")
                .append("Portfolio Name: ").append(portfolio.getName()).append("\n")
                .append("Description: ").append(portfolio.getDescription()).append("\n")
                .append("Total Value: ").append(portfolio.getTotalValue()).append("\n\n")
                .append("Assets:\n");

        for (Asset a : portfolio.getAssets()) {
            sb.append("- ").append(a.getTickerSymbol())
                    .append(" (").append(a.getCompanyName()).append(") ")
                    .append("| Quantity: ").append(a.getQuantity())
                    .append(" | Purchase Price: ").append(a.getPurchasePrice())
                    .append(" | Current Price: ").append(a.getCurrentPrice())
                    .append(" | Total Value: ").append(a.getTotalValue())
                    .append("\n");
        }

        sb.append("\nFocus on performance trends, sector diversification, "
                + "and opportunities or risks based on current market conditions.");
        return sb.toString();
    }


    public String getResponse(String q,String userName) {
      return   chatClient
                .prompt(q)
             // .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,userName))
              .system("you are expert in stock analyse and stock markest ")
                .user("You are a financial data assistant")
                .call()
              .content();
    }

    public SymbolSuggest findClosestStockSymbol(String userInput) {
        String userPrompt = """
        A user entered the text: "%s".
        Please return only the single most likely U.S. stock ticker symbol and company name
        that best matches this input. If nothing is close, answer "NONE".
        Do not explain—just return the symbol and company name.
        """.formatted(userInput);

        return chatClient
                .prompt()
                .system("You are a financial data assistant that specializes in matching free-text queries to U.S. stock symbols.")
                .user(userPrompt)
                .call()
                .entity(SymbolSuggest.class);
    }

}
