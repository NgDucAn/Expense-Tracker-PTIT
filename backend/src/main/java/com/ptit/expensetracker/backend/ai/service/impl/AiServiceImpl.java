package com.ptit.expensetracker.backend.ai.service.impl;

import com.ptit.expensetracker.backend.ai.domain.AiChatMessage;
import com.ptit.expensetracker.backend.ai.domain.MessageRole;
import com.ptit.expensetracker.backend.ai.dto.ChatRequest;
import com.ptit.expensetracker.backend.ai.dto.ChatResponse;
import com.ptit.expensetracker.backend.ai.dto.InsightsRequest;
import com.ptit.expensetracker.backend.ai.dto.InsightsResponse;
import com.ptit.expensetracker.backend.ai.dto.ParseTransactionRequest;
import com.ptit.expensetracker.backend.ai.dto.ParsedTransactionDto;
import com.ptit.expensetracker.backend.ai.repository.AiChatMessageRepository;
import com.ptit.expensetracker.backend.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final AiChatMessageRepository chatMessageRepository;

    @Override
    public ChatResponse chat(String userId, ChatRequest request) {
        // TODO: integrate with Gemini API in later steps.
        // For now we persist the user message and return a stubbed reply
        // so that the API contract and layers are in place.

        AiChatMessage userMessage = AiChatMessage.builder()
                .userId(userId)
                .role(MessageRole.USER)
                .content(request.getMessage())
                .createdAt(OffsetDateTime.now())
                .build();
        chatMessageRepository.save(userMessage);

        String reply = "AI backend is set up. Gemini integration will be added in the next phase.";

        AiChatMessage assistantMessage = AiChatMessage.builder()
                .userId(userId)
                .role(MessageRole.ASSISTANT)
                .content(reply)
                .createdAt(OffsetDateTime.now())
                .build();
        chatMessageRepository.save(assistantMessage);

        return ChatResponse.builder()
                .reply(reply)
                .suggestions(Collections.emptyList())
                .build();
    }

    @Override
    public ParsedTransactionDto parseTransaction(String userId, ParseTransactionRequest request) {
        // TODO: delegate to Gemini for natural language understanding.
        // Stub implementation to keep HTTP contract stable.
        return ParsedTransactionDto.builder()
                .amount(null)
                .currencyCode(null)
                .categoryName(null)
                .description(request.getText())
                .date(null)
                .walletName(null)
                .build();
    }

    @Override
    public InsightsResponse generateInsights(String userId, InsightsRequest request) {
        // TODO: call Gemini with structured financial summary.
        // Stub implementation with simple rule-based messages.
        String alert = request.getTotalExpense() > request.getTotalIncome()
                ? "Chi tiêu của bạn đang lớn hơn thu nhập trong giai đoạn này."
                : "Chi tiêu của bạn đang trong ngưỡng an toàn so với thu nhập.";

        String debtAlert = request.getTotalDebt() > 0
                ? "Bạn đang có các khoản nợ cần theo dõi cẩn thận."
                : "Hiện tại bạn không có khoản nợ nào được báo cáo.";

        return InsightsResponse.builder()
                .alerts(java.util.List.of(alert, debtAlert))
                .tips(Collections.singletonList(
                        "Hãy thiết lập ngân sách cho các nhóm chi tiêu lớn như ăn uống, giải trí để kiểm soát tốt hơn."))
                .build();
    }
}



