package com.ptit.expensetracker.features.money.ui.category

import com.ptit.expensetracker.core.platform.MviEventBase
import com.ptit.expensetracker.core.platform.MviIntentBase
import com.ptit.expensetracker.core.platform.MviStateBase
import com.ptit.expensetracker.features.money.domain.model.Category
import com.ptit.expensetracker.features.money.domain.model.CategoryGroup
import com.ptit.expensetracker.features.money.domain.model.CategoryType

data class CategoryState(
    val isLoading: Boolean = false,
    val allCategoryGroups: Map<CategoryType, List<CategoryGroup>> = emptyMap(),
    val categoryGroups: List<CategoryGroup> = emptyList(),
    val selectedTab: CategoryType = CategoryType.EXPENSE,
    val selectedCategory: Category? = null,
    val error: String? = null
): MviStateBase

sealed interface CategoryIntent: MviIntentBase {
    object LoadCategories : CategoryIntent
    data class SelectTab(val type: CategoryType) : CategoryIntent
    data class SelectCategory(val categoryId: Int) : CategoryIntent
}

sealed interface CategoryEvent: MviEventBase {
    data class CategorySelected(val category: Category) : CategoryEvent
}