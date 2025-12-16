package com.ptit.expensetracker.features.money.ui.addtransaction

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ptit.expensetracker.core.platform.BaseViewModel
import com.ptit.expensetracker.features.money.domain.model.TransactionType
import com.ptit.expensetracker.features.money.ui.addtransaction.components.NumpadButton
import com.ptit.expensetracker.features.money.domain.model.Category
import com.ptit.expensetracker.features.money.domain.model.CategoryType
import com.ptit.expensetracker.features.money.domain.model.Wallet
import com.ptit.expensetracker.features.money.domain.usecases.GetWalletsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.SaveTransactionUseCase
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.Contact
import com.ptit.expensetracker.utils.CalculatorUtil
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.ptit.expensetracker.features.money.data.data_source.local.CategoryDataSource
import com.ptit.expensetracker.features.money.domain.usecases.GetCategoryByNameUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetTransactionByIdUseCase
import com.ptit.expensetracker.features.money.domain.model.Transaction
import com.ptit.expensetracker.utils.formatAmountWithCurrency
import java.text.Normalizer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getWalletsUseCase: GetWalletsUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val getCategoryByNameUseCase: GetCategoryByNameUseCase,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase
) : BaseViewModel<AddTransactionState, AddTransactionIntent, AddTransactionEvent>(), CalculatorUtil.CalculatorCallback {

    override val _viewState = MutableStateFlow(AddTransactionState())

    private val calculatorUtil = CalculatorUtil(this)

    // Load categories from JSON/assets for offline classification
    private val offlineCategories: List<Category> by lazy {
        CategoryDataSource().loadCategoryEntities(context)
            .map { it.toCategory() }
    }

    // Normalize input string (lowercase, trim, remove accents)
    private fun normalize(input: String): String {
        val lower = input.trim().lowercase()
        val normalized = Normalizer.normalize(lower, Normalizer.Form.NFD)
        // Remove diacritical marks
        return normalized.replace(Regex("\\p{Mn}+"), "")
    }

    // Attempt to find a matching category offline (exact metadata match or via synonyms)
    private fun findCategoryOffline(name: String): Category? {
        val norm = normalize(name)
        // 1. Exact match on metadata (digits removed, accent-insensitive)
        offlineCategories.firstOrNull { cat ->
            normalize(cat.metaData.filter { ch -> !ch.isDigit() }) == norm
        }?.let { return it }
        // 2. Match via synonyms arrays (resource arrays named like "<category>_category_synonyms")
        offlineCategories.forEach { cat ->
            // Use title resource key (e.g., "cate_food") to derive base name
            val base = cat.title.removePrefix("cate_").lowercase()
            val resName = "${base}_category_synonyms"
            val resId = context.resources.getIdentifier(resName, "array", context.packageName)
            if (resId != 0) {
                context.resources.getStringArray(resId)
                    .map { normalize(it) }
                    .firstOrNull { it == norm }
                    ?.let { return cat }
            }
        }
        return null
    }

    init {
        // Wallets will be loaded on demand:
        // - For add mode: when screen is first loaded
        // - For edit mode: wallet comes from transaction data
    }

    private fun loadWallets() {
        getWalletsUseCase(
            params = UseCase.None(),
            scope = viewModelScope
        ) { result ->
            result.fold(
                { failure ->
                    // Handle error
                    emitEvent(AddTransactionEvent.ShowError("Failed to load wallets"))
                },
                { walletsFlow ->
                    walletsFlow
                        .onEach { wallets ->
                            if (wallets.isNotEmpty()) {
                                val firstWallet = wallets.first()
                                _viewState.value = _viewState.value.copy(
                                    wallet = firstWallet,
                                    isValidWallet = true
                                )

                                calculatorUtil.setCurrency(firstWallet.currency)
                            }
                        }
                        .catch { e ->
                            emitEvent(AddTransactionEvent.ShowError("Failed to load wallets: ${e.message}"))
                        }
                        .launchIn(viewModelScope)
                }
            )
        }
    }

    override fun processIntent(intent: AddTransactionIntent) {
        when (intent) {
            is AddTransactionIntent.UpdateAmount -> updateAmount(intent.amount)
            is AddTransactionIntent.SelectWallet -> selectWallet(intent.wallet)
            is AddTransactionIntent.SelectCategory -> selectCategory(intent.category)
            is AddTransactionIntent.UpdateDescription -> updateDescription(intent.description)
            is AddTransactionIntent.InitializeDescription -> initializeDescription(intent.description)
            is AddTransactionIntent.SelectTransactionType -> selectTransactionType(intent.type)
            is AddTransactionIntent.SelectDate -> selectDate(intent.date)
            is AddTransactionIntent.NumpadPressed -> handleNumpadPress(intent.button)
            is AddTransactionIntent.ToggleMoreDetails -> toggleMoreDetails()
            is AddTransactionIntent.ShowCategorySelector -> showCategorySelector()
            is AddTransactionIntent.ShowWalletSelector -> showWalletSelector()
            is AddTransactionIntent.ShowDatePicker -> showDatePicker()
            is AddTransactionIntent.Save -> saveTransaction()
            
            // Contact-related intents
            is AddTransactionIntent.UpdateContacts -> updateContacts(intent.contacts)
            is AddTransactionIntent.RemoveContact -> removeContact(intent.contact)
            is AddTransactionIntent.ShowContactSelector -> showContactSelector()
            
            is AddTransactionIntent.UpdateEventName -> updateEventName(intent.event)
            is AddTransactionIntent.SetReminder -> setReminder(intent.hasReminder)
            is AddTransactionIntent.UpdateExcludeFromReport -> updateExcludeFromReport(intent.exclude)
            is AddTransactionIntent.UpdatePhotoUri -> updatePhotoUri(intent.uri)
            is AddTransactionIntent.InitializeCategory -> initializeCategory(intent.categoryName)
            is AddTransactionIntent.InitializeDate -> initializeDate(intent.dateString)
            is AddTransactionIntent.ShowNumpad -> showNumpad()
            is AddTransactionIntent.HideNumpad -> hideNumpad()
            is AddTransactionIntent.ShowImageSourceDialog -> showImageSourceDialog()
            is AddTransactionIntent.HideImageSourceDialog -> hideImageSourceDialog()
            is AddTransactionIntent.SelectFromCamera -> selectFromCamera()
            is AddTransactionIntent.SelectFromGallery -> selectFromGallery()
            is AddTransactionIntent.RemovePhoto -> removePhoto()
            
            // Edit mode intents
            is AddTransactionIntent.LoadTransactionForEdit -> loadTransactionForEdit(intent.transactionId)
            is AddTransactionIntent.CancelEdit -> cancelEdit()
            
            // Initialization intent
            is AddTransactionIntent.InitializeForAddMode -> initializeForAddMode()
        }
    }


    override fun onCalculatorUpdate(
        amount: String,
        formattedAmount: String,
        displayExpression: String
    ) {
        _viewState.value = _viewState.value.copy(
            amountInput = amount,
            amount = amount.toDoubleOrNull() ?: 0.0,
            formattedAmount = formattedAmount,
            displayExpression = displayExpression
        )
    }

    override fun onError(
        message: String,
        level: CalculatorUtil.CalculatorError
    ) {

    }

    override fun onSaveAmount(amount: String, formattedAmount: String) {
        _viewState.value = _viewState.value.copy(
            amountInput = amount,
            amount = amount.toDoubleOrNull() ?: 0.0,
            formattedAmount = formattedAmount,
            showNumpad = false
        )
    }

    private fun updateAmount(amount: String) {
        calculatorUtil.updateAmountFromAssistant(amount)
    }

    private fun selectWallet(wallet: Wallet) {
        _viewState.value = _viewState.value.copy(
            wallet = wallet,
            isValidWallet = true,
            showWallets = false
        )

        calculatorUtil.setCurrency(wallet.currency)
    }

    private fun selectCategory(category: Category) {
        _viewState.value = _viewState.value.copy(
            category = category,
            isValidCategory = category != null,
            showCategories = false
        )
    }

    private fun updateDescription(description: String) {
        _viewState.value = _viewState.value.copy(description = description)
    }

    private fun selectTransactionType(type: TransactionType) {
        _viewState.value = _viewState.value.copy(transactionType = type)
    }

    private fun selectDate(date: Date) {
        viewModelScope.launch {
            try {
                _viewState.value = _viewState.value.copy(
                    transactionDate = date,
                    showDatePicker = false
                )
            } catch (e: Exception) {
                Log.e("AddTransactionVM", "Error selecting date: ${e.message}")
                emitEvent(AddTransactionEvent.ShowError("Failed to set transaction date"))
            }
        }
    }

    private fun toggleMoreDetails() {
        _viewState.value = _viewState.value.copy(
            showMoreDetails = !_viewState.value.showMoreDetails
        )
    }

    private fun showCategorySelector() {
        _viewState.value = _viewState.value.copy(showCategories = true)
    }

    private fun showWalletSelector() {
        _viewState.value = _viewState.value.copy(showWallets = true)
    }

    private fun showDatePicker() {
        _viewState.value = _viewState.value.copy(showDatePicker = true)
    }

    private fun handleNumpadPress(button: NumpadButton) {
        calculatorUtil.handleNumpadButtonPressed(button)
    }


    private fun saveTransaction() {
        viewModelScope.launch {
            try {
                _viewState.value = _viewState.value.copy(isSaving = true)

                // Validate transaction data
                val currentState = _viewState.value
                if (!currentState.isValidAmount || !currentState.isValidCategory || !currentState.isValidWallet) {
                    _viewState.value = _viewState.value.copy(
                        isSaving = false,
                        error = IllegalStateException("Please fill in all required fields")
                    )
                    emitEvent(AddTransactionEvent.ShowError("Please fill in all required fields"))
                    return@launch
                }

                // Determine transaction type based on category type
                val transactionType = when (currentState.category!!.type) {
                    CategoryType.INCOME -> TransactionType.INFLOW
                    CategoryType.EXPENSE -> TransactionType.OUTFLOW
                    CategoryType.DEBT_LOAN -> {
                        // For debt/loan categories, we need more specific logic
                        when (currentState.category.metaData) {
                            "IS_DEBT", "IS_LOAN" -> TransactionType.OUTFLOW
                            "IS_DEBT_COLLECTION", "IS_REPAYMENT" -> TransactionType.INFLOW
                            else -> currentState.transactionType // Keep current if unknown
                        }
                    }
                    CategoryType.UNKNOWN -> currentState.transactionType // Keep current if unknown
                }

                // Log for debugging
                Log.d("Transaction", "Category: ${currentState.category.title}, Type: ${currentState.category.type}")
                Log.d("Transaction", "Determined transaction type: $transactionType")

                val withPersons = Gson().toJson(currentState.withContacts)
                Log.d("Transaction", "currentState.withContacts: $withPersons")


                // Save or update transaction using SaveTransactionUseCase
                val params = SaveTransactionUseCase.Params(
                    transactionId = currentState.editingTransactionId ?: 0, // Use existing ID for edit, 0 for new
                    wallet = currentState.wallet,
                    amount = currentState.amount.toDouble(),
                    transactionType = transactionType,
                    transactionDate = currentState.transactionDate,
                    description = currentState.description.ifEmpty { null },
                    category = currentState.category!!,
                    withPerson = withPersons,
                    eventName = currentState.eventName.ifEmpty { null },
                    hasReminder = currentState.reminderSet,
                    excludeFromReport = currentState.excludeFromReport,
                    photoUri = currentState.photoUri
                )

                saveTransactionUseCase(params, viewModelScope) { result ->
                    result.fold(
                        { failure ->
                            _viewState.value = _viewState.value.copy(
                                isSaving = false,
                                error = Exception(failure.toString())
                            )
                            val errorMessage = if (currentState.isEditMode) {
                                "Failed to update transaction"
                            } else {
                                "Failed to save transaction"
                            }
                            emitEvent(AddTransactionEvent.ShowError(errorMessage))
                        },
                        { transactionId ->
                            _viewState.value = _viewState.value.copy(
                                isSaving = false,
                                isSuccess = true
                            )
                            
                            // Emit appropriate success event
                            if (currentState.isEditMode) {
                                emitEvent(AddTransactionEvent.TransactionUpdated)
                            } else {
                                emitEvent(AddTransactionEvent.TransactionSaved)
                            }
                            emitEvent(AddTransactionEvent.NavigateBack)
                        }
                    )
                }

            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(
                    isSaving = false,
                    error = e
                )
                emitEvent(AddTransactionEvent.ShowError(e.message ?: "An error occurred"))
            }
        }
    }

    private fun updateContacts(contacts: List<Contact>) {
        _viewState.value = _viewState.value.copy(withContacts = contacts)
    }

    private fun removeContact(contactToRemove: Contact) {
        val currentContacts = _viewState.value.withContacts
        val updatedContacts = currentContacts.filter { it.id != contactToRemove.id }
        _viewState.value = _viewState.value.copy(withContacts = updatedContacts)
    }

    private fun showContactSelector() {
        emitEvent(AddTransactionEvent.NavigateToContacts(_viewState.value.withContacts))
    }

    private fun updateEventName(event: String) {
        _viewState.value = _viewState.value.copy(eventName = event)
    }

    private fun setReminder(hasReminder: Boolean) {
        _viewState.value = _viewState.value.copy(reminderSet = hasReminder)
    }

    private fun updateExcludeFromReport(exclude: Boolean) {
        _viewState.value = _viewState.value.copy(excludeFromReport = exclude)
    }

    private fun updatePhotoUri(uri: String?) {
        _viewState.value = _viewState.value.copy(photoUri = uri)
    }

    // Handle initializing a category from a category name/metadata received from AI
    private fun initializeCategory(categoryName: String) {
        viewModelScope.launch {
            // 1) Try match by metadata/name directly (case-insensitive, accent-insensitive)
            findCategoryByMetadataOrName(categoryName)?.let { cat ->
                val resolved = resolveCategoryFromDbOrFallback(cat.metaData)
                selectCategory(resolved ?: cat)
                Log.d("AddTransactionVM", "Matched category by metadata/name: ${cat.metaData}")
                return@launch
            }
            // 2) Offline classification via synonyms/metadata
            findCategoryOffline(categoryName)?.let { matched ->
                val resolved = resolveCategoryFromDbOrFallback(matched.metaData)
                selectCategory(resolved ?: matched)
                return@launch
            }
            // 3) Fallback placeholder
            selectCategory(
                Category(
                    id = 0,
                    metaData = "default",
                    title = categoryName,
                    icon = "ic_category_unknown",
                    type = CategoryType.EXPENSE,
                    parentName = "other"
                )
            )
            Log.d("AddTransactionVM", "Defaulted to placeholder category: $categoryName")
        }
    }

    private suspend fun resolveCategoryFromDbOrFallback(metaOrName: String): Category? {
        return resolveCategory(metaOrName)
            ?: resolveCategory("IS_OTHER_EXPENSE")
    }

    private suspend fun resolveCategory(metaOrName: String): Category? = suspendCoroutine { cont ->
        getCategoryByNameUseCase(
            params = GetCategoryByNameUseCase.Params(metaOrName),
            scope = viewModelScope
        ) { result ->
            result.fold(
                { _ -> cont.resume(null) },
                { cat -> cont.resume(cat) }
            )
        }
    }

    private fun findCategoryByMetadataOrName(input: String): Category? {
        val normInput = normalize(input)
        return offlineCategories.firstOrNull { cat ->
            normalize(cat.metaData) == normInput ||
            normalize(cat.title) == normInput ||
            (cat.parentName != null && normalize(cat.parentName) == normInput)
        }
    }
    
    // Handle initializing a date from a date string received from Google Assistant
    private fun initializeDate(dateString: String) {
        viewModelScope.launch {
            try {
                // Parse the date string - expected format may vary based on Assistant output
                // Common formats might be "yyyy-MM-dd", "MM/dd/yyyy", etc.
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                val date = try {
                    dateFormat.parse(dateString) ?: Date()
                } catch (e: Exception) {
                    // If parsing fails, try alternate formats
                    try {
                        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(dateString) ?: Date()
                    } catch (e: Exception) {
                        // If all parsing attempts fail, use current date
                        Date()
                    }
                }
                
                selectDate(date)
                Log.d("AddTransactionVM", "Initialized date from string: $dateString to date: $date")
            } catch (e: Exception) {
                Log.e("AddTransactionVM", "Error initializing date: ${e.message}")
                // Fallback to current date
                selectDate(Date())
            }
        }
    }

    private fun showNumpad() {
        _viewState.value = _viewState.value.copy(showNumpad = true)
    }

    private fun initializeDescription(description: String) {
        _viewState.value = _viewState.value.copy(description = description)
    }

    private fun hideNumpad() {
        _viewState.value = _viewState.value.copy(showNumpad = false)
    }

    private fun showImageSourceDialog() {
        _viewState.value = _viewState.value.copy(showImageSourceDialog = true)
    }

    private fun hideImageSourceDialog() {
        _viewState.value = _viewState.value.copy(showImageSourceDialog = false)
    }

    private fun selectFromCamera() {
        hideImageSourceDialog()
        emitEvent(AddTransactionEvent.LaunchCamera)
    }

    private fun selectFromGallery() {
        hideImageSourceDialog()
        emitEvent(AddTransactionEvent.LaunchGallery)
    }

    private fun removePhoto() {
        _viewState.value = _viewState.value.copy(photoUri = null)
    }
    
    // Edit mode methods
    private fun loadTransactionForEdit(transactionId: Int) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(
                isEditMode = true,
                editingTransactionId = transactionId,
                isLoadingTransaction = true
            )
            
            getTransactionByIdUseCase(
                params = transactionId,
                scope = viewModelScope
            ) { result ->
                result.fold(
                    { failure ->
                        _viewState.value = _viewState.value.copy(isLoadingTransaction = false)
                        emitEvent(AddTransactionEvent.FailedToLoadTransaction(failure.toString()))
                    },
                    { transaction ->
                        populateStateFromTransaction(transaction)
                    }
                )
            }
        }
    }
    
    private fun populateStateFromTransaction(transaction: Transaction) {
        // Parse contacts from JSON string
        val contacts = try {
            if (!transaction.withPerson.isNullOrEmpty()) {
                Gson().fromJson(transaction.withPerson, Array<Contact>::class.java)?.toList() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.w("AddTransactionVM", "Failed to parse contacts: ${e.message}")
            emptyList()
        }

        _viewState.value = _viewState.value.copy(
            isLoadingTransaction = false,
            wallet = transaction.wallet,
            category = transaction.category,
            transactionType = transaction.transactionType,
            description = transaction.description ?: "",
            transactionDate = transaction.transactionDate,
            
            // Additional details
            withContacts = contacts,
            eventName = transaction.eventName ?: "",
            reminderSet = transaction.hasReminder,
            excludeFromReport = transaction.excludeFromReport,
            photoUri = transaction.photoUri,
            
            // Auto-expand details if has additional data
            showMoreDetails = hasAdditionalDetails(transaction),
            
            // Set validation flags
            isValidAmount = transaction.amount > 0,
            isValidCategory = true,
            isValidWallet = true
        )
        
        // Initialize calculator with loaded amount and currency
        calculatorUtil.setCurrency(transaction.wallet.currency)
        calculatorUtil.updateAmountFromAssistant(transaction.amount.toString())
    }
    
    private fun hasAdditionalDetails(transaction: Transaction): Boolean {
        return !transaction.withPerson.isNullOrEmpty() ||
               !transaction.eventName.isNullOrEmpty() ||
               transaction.hasReminder ||
               transaction.excludeFromReport ||
               !transaction.photoUri.isNullOrEmpty()
    }
    
    private fun cancelEdit() {
        // TODO: Will be implemented in Phase 2
        _viewState.value = _viewState.value.copy(
            isEditMode = false,
            editingTransactionId = null,
            isLoadingTransaction = false
        )
        emitEvent(AddTransactionEvent.NavigateBack)
    }
    
    private fun initializeForAddMode() {
        // Only load wallets if not in edit mode
        if (!_viewState.value.isEditMode) {
            loadWallets()
        }
    }

}