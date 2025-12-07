package com.ptit.expensetracker.backend.ai.service;

import com.ptit.expensetracker.backend.ai.dto.ChatRequest;
import com.ptit.expensetracker.backend.ai.dto.ChatResponse;
import com.ptit.expensetracker.backend.ai.dto.InsightsRequest;
import com.ptit.expensetracker.backend.ai.dto.InsightsResponse;
import com.ptit.expensetracker.backend.ai.dto.ParseTransactionRequest;
import com.ptit.expensetracker.backend.ai.dto.ParsedTransactionDto;

public interface AiService {

    ChatResponse chat(String userId, ChatRequest request);

    ParsedTransactionDto parseTransaction(String userId, ParseTransactionRequest request);

    InsightsResponse generateInsights(String userId, InsightsRequest request);
}



