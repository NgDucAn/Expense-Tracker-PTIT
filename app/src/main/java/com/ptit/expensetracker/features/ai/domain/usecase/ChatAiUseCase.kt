package com.ptit.expensetracker.features.ai.domain.usecase

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.functional.toLeft
import com.ptit.expensetracker.core.functional.toRight
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.ai.data.AiRepository
import com.ptit.expensetracker.features.ai.data.remote.dto.ChatRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.ChatResponseDto
import com.ptit.expensetracker.features.ai.domain.AiFailure
import java.net.SocketTimeoutException
import javax.inject.Inject

class ChatAiUseCase @Inject constructor(
    private val repository: AiRepository
) : UseCase<ChatResponseDto, ChatAiUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, ChatResponseDto> {
        return try {
            repository.chat(
                ChatRequestDto(
                    message = params.message,
                    locale = params.locale,
                    context = params.context
                )
            ).toRight()
        } catch (e: SocketTimeoutException) {
            Failure.NetworkConnection.toLeft()
        } catch (e: Exception) {
            AiFailure.Unknown(e.message ?: "AI chat error").toLeft()
        }
    }

    data class Params(
        val message: String,
        val locale: String? = null,
        val context: String? = null
    )
}


