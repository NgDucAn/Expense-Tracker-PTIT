# Expense Tracker Backend (AI Gateway)

This Spring Boot service acts as a thin backend for the Expense Tracker PTIT mobile app.
It is responsible for:

- Exposing AI-related APIs (chat, transaction parsing, insights).
- Verifying Firebase ID tokens (to be implemented next).
- Acting as a secure gateway to external LLM providers (e.g. Gemini).

## High-level API Contract

- `POST /api/ai/chat`
  - Request: `ChatRequest { message: string, locale?: string, context?: string }`
  - Response: `ChatResponse { reply: string, suggestions?: string[] }`

- `POST /api/ai/parse-transaction`
  - Request: `ParseTransactionRequest { text: string, locale?: string }`
  - Response: `ParsedTransactionDto { amount?: number, currencyCode?: string, categoryName?: string, description?: string, date?: LocalDate, walletName?: string }`

- `POST /api/ai/insights`
  - Request: `InsightsRequest { totalIncome: number, totalExpense: number, totalDebt: number, recentSpendingPattern?: string, timeRange?: string }`
  - Response: `InsightsResponse { alerts: string[], tips: string[] }`

Authentication
--------------

- All endpoints require **Firebase ID token**:
  - Header: `Authorization: Bearer <id_token>`
  - Token is verified server-side via Firebase Admin SDK.
- Test endpoint: `GET /api/me` returns `{ "userId": "<firebase_uid>" }` when token is valid.


