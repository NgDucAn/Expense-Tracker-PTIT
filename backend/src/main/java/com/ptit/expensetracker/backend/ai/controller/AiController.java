package com.ptit.expensetracker.backend.ai.controller;

import com.ptit.expensetracker.backend.ai.dto.ChatRequest;
import com.ptit.expensetracker.backend.ai.dto.ChatResponse;
import com.ptit.expensetracker.backend.ai.dto.InsightsRequest;
import com.ptit.expensetracker.backend.ai.dto.InsightsResponse;
import com.ptit.expensetracker.backend.ai.dto.ParseTransactionRequest;
import com.ptit.expensetracker.backend.ai.dto.ParsedTransactionDto;
import com.ptit.expensetracker.backend.ai.service.AiService;
import com.ptit.expensetracker.backend.common.exception.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST API entrypoint for AI-related operations (chat, transaction parsing, insights).
 * <p>
 * Authentication:
 * In later steps, each request will be authenticated via Firebase ID token and
 * the user id will be resolved from the security context. For now we accept
 * a simple "X-Debug-User-Id" header for local testing.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            Authentication authentication,
            @Valid @RequestBody ChatRequest request) {

        String userId = resolveUserId(authentication);
        ChatResponse response = aiService.chat(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/parse-transaction")
    public ResponseEntity<ParsedTransactionDto> parseTransaction(
            Authentication authentication,
            @Valid @RequestBody ParseTransactionRequest request) {

        String userId = resolveUserId(authentication);
        ParsedTransactionDto dto = aiService.parseTransaction(userId, request);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/insights")
    public ResponseEntity<InsightsResponse> generateInsights(
            Authentication authentication,
            @Valid @RequestBody InsightsRequest request) {

        String userId = resolveUserId(authentication);
        InsightsResponse response = aiService.generateInsights(userId, request);
        return ResponseEntity.ok(response);
    }

    private String resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }
        return authentication.getName();
    }
}


