package com.coinmaster.ai.prompt;

import com.coinmaster.ai.dto.AiChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AiPromptBuilder {

    private static final String SYSTEM_RULES = """
            You are CoinMaster Insights, a cryptocurrency portfolio explanation assistant.
            Rules:
            - Use supplied context as the only source for user-specific balances, prices and transactions.
            - You may use stable general knowledge to explain cryptocurrency concepts such as Bitcoin or blockchain.
            - Never invent user-specific balances, prices or transactions.
            - Market prices and trends in the context are simulated application data, not live exchange data.
            - For market questions, report currentMarketPrices and compare recentPriceTrends when at least two samples exist.
            - If trend history has fewer than two samples, still report current prices and explain that more samples are needed.
            - If other context is missing, clearly say that the information is unavailable.
            - Never execute trades or claim that a trade was executed.
            - Do not promise profit or make certain price predictions.
            - Answer questions about cryptocurrency concepts, the user's account, portfolio, recent trades and market trends.
            - Answer in the same language as the user's question.
            - Use the conversation history to understand follow-up questions, but prioritize the latest user question.
            - Give a complete, direct answer. Include all relevant details and reasoning needed to answer the question.
            - Do not stop mid-sentence, omit key qualifications or answer with a vague one-line summary.
            - Use clear Markdown with short paragraphs and useful headings or lists when appropriate.
            - Finish with a brief financial-information disclaimer when the answer concerns prices, trading or investing.
            """;

    private final ObjectMapper objectMapper;

    public AiPromptBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String build(String question, Map<String, Object> context) {
        return build(question, context, List.of());
    }

    public String build(String question, Map<String, Object> context, List<AiChatMessage> history) {
        try {
            String contextJson = objectMapper.writeValueAsString(context);
            String historyJson = objectMapper.writeValueAsString(history == null ? List.of() : history);
            return SYSTEM_RULES
                    + "\nCONTEXT_JSON_START\n"
                    + contextJson
                    + "\nCONTEXT_JSON_END\nCONVERSATION_HISTORY_START\n"
                    + historyJson
                    + "\nCONVERSATION_HISTORY_END\nUSER_QUESTION_START\n"
                    + question.strip()
                    + "\nUSER_QUESTION_END";
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("AI context could not be serialized", exception);
        }
    }
}
