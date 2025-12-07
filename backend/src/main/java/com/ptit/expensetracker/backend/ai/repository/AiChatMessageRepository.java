package com.ptit.expensetracker.backend.ai.repository;

import com.ptit.expensetracker.backend.ai.domain.AiChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {

    List<AiChatMessage> findTop20ByUserIdOrderByCreatedAtDesc(String userId);
}



