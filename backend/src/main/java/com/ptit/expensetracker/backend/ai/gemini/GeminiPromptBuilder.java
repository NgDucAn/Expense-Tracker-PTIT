package com.ptit.expensetracker.backend.ai.gemini;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class GeminiPromptBuilder {

    public String buildChatPrompt(String userMessage, String userId, String locale) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bạn là trợ lý quản lý chi tiêu cá nhân, trả lời ngắn gọn, bằng tiếng Việt. ");
        sb.append("Vai trò: hỗ trợ người dùng kiểm soát chi tiêu, gợi ý hành động thực tế. ");
        sb.append("Không bịa số liệu; nếu thiếu dữ liệu, hãy hỏi lại. ");
        if (locale != null && !locale.isBlank()) {
            sb.append("Locale của người dùng: ").append(locale).append(". ");
        }
        sb.append("UserId (ẩn danh): ").append(userId).append(". ");
        sb.append("Tin nhắn của người dùng: ").append(userMessage);
        return sb.toString();
    }

    public String buildParseTransactionPrompt(String text, String locale) {
        StringBuilder sb = new StringBuilder();
        LocalDate currentDateUtc = LocalDate.now(ZoneOffset.UTC);
        sb.append("Bạn là trợ lý quản lý chi tiêu. Nhiệm vụ: trích xuất giao dịch từ câu tiếng Việt. ");
        sb.append("Phải trả về JSON THUẦN (không markdown, không giải thích). ");
        sb.append("Schema bắt buộc: {\"amount\": number, \"currencyCode\": string, \"categoryName\": string, \"description\": string, \"date\": \"YYYY-MM-DD\", \"walletName\": string}. ");
        sb.append("Nếu thiếu thông tin, để null hoặc chuỗi rỗng nhưng vẫn trả về JSON hợp lệ. ");

        String today = currentDateUtc.format(DateTimeFormatter.ISO_DATE);
        sb.append("Thời gian hiện tại (UTC) là ").append(today).append(". ");

        if (locale != null && !locale.isBlank()) {
            sb.append("Locale: ").append(locale).append(". ");
        }
        sb.append("Câu người dùng: ").append(text);
        return sb.toString();
    }

    public String buildInsightsPrompt(
            double totalIncome,
            double totalExpense,
            double totalDebt,
            String recentPattern,
            String timeRange,
            LocalDate currentDateUtc
    ) {
        String today = currentDateUtc != null
                ? currentDateUtc.format(DateTimeFormatter.ISO_DATE)
                : "unknown";

        StringBuilder sb = new StringBuilder();
        sb.append("Bạn là trợ lý tài chính cá nhân, trả lời ngắn gọn bằng tiếng Việt. ");
        sb.append("Nhiệm vụ: sinh cảnh báo (alerts) và gợi ý (tips) dựa trên số liệu chi tiêu. ");
        sb.append("Phải trả về JSON THUẦN, schema: {\"alerts\": [string], \"tips\": [string]}. ");
        sb.append("Mỗi mục ngắn gọn, hành động, không lan man, không markdown. ");
        sb.append("Ngày hiện tại (UTC): ").append(today).append(". ");
        sb.append("Tổng thu nhập: ").append(totalIncome).append(". ");
        sb.append("Tổng chi tiêu: ").append(totalExpense).append(". ");
        sb.append("Tổng nợ: ").append(totalDebt).append(". ");
        if (timeRange != null && !timeRange.isBlank()) {
            sb.append("Khoảng thời gian: ").append(timeRange).append(". ");
        }
        if (recentPattern != null && !recentPattern.isBlank()) {
            sb.append("Mô tả xu hướng gần đây: ").append(recentPattern).append(". ");
        }
        sb.append("Trả về tối đa 3 alerts và 3 tips. Nếu không có gì đáng lưu ý, để mảng trống.");
        return sb.toString();
    }
}


