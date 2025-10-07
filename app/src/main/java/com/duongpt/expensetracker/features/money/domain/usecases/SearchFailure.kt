package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure

/**
 * Custom failure types for search operations
 */
sealed class SearchFailure : Failure.FeatureFailure() {
    object InvalidAmountRange : SearchFailure()
    object InvalidDateRange : SearchFailure()
    object EmptySearchQuery : SearchFailure()
    object SearchTextTooShort : SearchFailure()
} 