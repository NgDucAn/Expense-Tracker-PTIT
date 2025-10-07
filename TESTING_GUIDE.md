# Phase 5: Testing & QA Guide

## Monthly Transaction Tab System Testing

### 1. Month Tab Generation Testing

#### Test Case 1.1: Default Month List
- **Description**: Verify default month list contains 20 tabs
- **Steps**:
  1. Open Transaction screen
  2. Count total tabs in month selector
- **Expected**: 18 past months + "This month" + "Future" = 20 tabs total

#### Test Case 1.2: Tab Labels Format
- **Description**: Verify month labels are formatted correctly
- **Steps**:
  1. Open Transaction screen
  2. Scroll through all month tabs
- **Expected**: 
  - Past months: "MM/yyyy" format (e.g., "01/2024", "12/2023")
  - Current month: "This month"
  - Future: "Future"

#### Test Case 1.3: Tab Chronological Order
- **Description**: Verify tabs are in chronological order
- **Steps**:
  1. Open Transaction screen
  2. Scroll through tabs from left to right
- **Expected**: Oldest month → Recent months → "This month" → "Future"

### 2. Tab Selection and Data Loading

#### Test Case 2.1: Default Tab Selection
- **Description**: Verify app defaults to "This month" tab
- **Steps**:
  1. Fresh app launch or navigate to Transaction screen
- **Expected**: "This month" tab is selected by default

#### Test Case 2.2: Month Tab Transaction Loading
- **Description**: Verify transactions load correctly for each month
- **Steps**:
  1. Select different month tabs (3-4 different months)
  2. Observe loading indicator and transaction data
- **Expected**: 
  - Loading indicator appears during data fetch
  - Correct transactions displayed for each month
  - No transactions from other months mixed in

#### Test Case 2.3: Future Tab Transaction Loading
- **Description**: Verify future transactions load correctly
- **Steps**:
  1. Select "Future" tab
  2. Observe transactions displayed
- **Expected**: 
  - Only transactions with future dates shown
  - Transactions sorted earliest to latest (ascending)

### 3. Wallet Integration Testing

#### Test Case 3.1: Individual Wallet Month Filtering
- **Description**: Verify month filtering works with individual wallets
- **Steps**:
  1. Select a specific wallet (not "Total Wallet")
  2. Switch between different month tabs
- **Expected**: Only transactions from selected wallet and month shown

#### Test Case 3.2: Total Wallet Month Filtering
- **Description**: Verify month filtering works with Total Wallet
- **Steps**:
  1. Select "Total Wallet"
  2. Switch between different month tabs
- **Expected**: Transactions from all wallets for selected month shown

#### Test Case 3.3: Wallet Switch Preserves Tab Selection
- **Description**: Verify tab selection is maintained when switching wallets
- **Steps**:
  1. Select a specific month tab (e.g., "02/2024")
  2. Switch to different wallet
- **Expected**: Same month tab remains selected, data refreshes for new wallet

### 4. Currency Conversion Testing (Total Wallet)

#### Test Case 4.1: Multi-Currency Inflow/Outflow Calculation
- **Description**: Verify correct currency conversion for Total Wallet view
- **Steps**:
  1. Create wallets with different currencies (USD, EUR, VND)
  2. Add transactions to each wallet in different months
  3. Select "Total Wallet" option
  4. Switch between month tabs
  5. Verify inflow/outflow totals are converted to main currency
- **Expected**: All amounts converted to main wallet currency, totals accurate

#### Test Case 4.2: Daily Total Conversion
- **Description**: Test daily transaction totals with mixed currencies
- **Steps**:
  1. Add transactions in different currencies on same date
  2. Open Total Wallet view
  3. Check daily total headers
- **Expected**: Daily totals show converted amounts in main currency

#### Test Case 4.3: Currency Conversion Fallback
- **Description**: Test behavior when currency conversion fails
- **Steps**:
  1. Create transaction with unknown/unsupported currency
  2. Switch to Total Wallet view
- **Expected**: Graceful fallback to original amount, no crashes

#### Test Case 4.4: Same Currency Optimization
- **Description**: Verify no conversion when all wallets use same currency
- **Steps**:
  1. Create multiple wallets with same currency
  2. Switch to Total Wallet view
  3. Verify calculations are direct (no conversion overhead)
- **Expected**: Fast calculations, accurate totals without conversion

### 5. Data Accuracy Testing

#### Test Case 5.1: Month Summary Calculations
- **Description**: Verify inflow/outflow calculations for each month
- **Steps**:
  1. Select a month with known transaction data
  2. Manually calculate expected inflow/outflow
  3. Compare with displayed summary
- **Expected**: Summary matches manual calculations

#### Test Case 5.2: Daily Transaction Grouping
- **Description**: Verify transactions are grouped correctly by date
- **Steps**:
  1. Select a month with multiple transactions on same dates
  2. Check transaction grouping
- **Expected**: 
  - Transactions grouped by date
  - Daily totals calculated correctly
  - Dates sorted newest to oldest within month

#### Test Case 5.3: Empty Month Handling
- **Description**: Verify handling of months with no transactions
- **Steps**:
  1. Select a month with no transactions
- **Expected**: 
  - No transaction cards displayed
  - Summary shows 0 inflow/outflow
  - No error messages

### 5. Performance Testing

#### Test Case 5.1: Tab Switching Performance
- **Description**: Verify smooth tab switching without delays
- **Steps**:
  1. Rapidly switch between different month tabs
  2. Observe response time and UI smoothness
- **Expected**: 
  - Fast tab switching (< 500ms)
  - Smooth scrolling in tab bar
  - No UI freezing

#### Test Case 5.2: Large Dataset Handling
- **Description**: Verify performance with large transaction datasets
- **Steps**:
  1. Test with wallets containing 100+ transactions per month
  2. Switch between tabs
- **Expected**: 
  - Consistent performance regardless of data size
  - Memory usage remains stable

### 6. Edge Cases Testing

#### Test Case 6.1: Month Boundary Transactions
- **Description**: Verify transactions at month boundaries
- **Steps**:
  1. Check transactions on last day of month vs first day of next month
- **Expected**: Each transaction appears in correct month tab

#### Test Case 6.2: Future Date Validation
- **Description**: Verify future transactions appear only in Future tab
- **Steps**:
  1. Create transaction with future date
  2. Check if it appears in Future tab only
- **Expected**: Future transaction only in "Future" tab

#### Test Case 6.3: Timezone Handling
- **Description**: Verify date calculations respect device timezone
- **Steps**:
  1. Test with transactions created near midnight
- **Expected**: Transactions grouped by local date correctly

### 7. UI/UX Testing

#### Test Case 7.1: Tab Indicator Visual
- **Description**: Verify tab indicator shows selected tab clearly
- **Steps**:
  1. Switch between tabs
  2. Observe visual indicator
- **Expected**: Clear visual indication of selected tab

#### Test Case 7.2: Tab Scrolling
- **Description**: Verify tab bar scrolls smoothly through all months
- **Steps**:
  1. Scroll through all 20 tabs
- **Expected**: 
  - Smooth horizontal scrolling
  - All tabs accessible
  - Edge padding appropriate

#### Test Case 7.3: Loading States
- **Description**: Verify proper loading states during data fetch
- **Steps**:
  1. Switch tabs and observe loading states
- **Expected**: 
  - Loading indicator during data fetch
  - Previous data cleared before new data loads

### 8. Error Handling Testing

#### Test Case 8.1: Database Error Handling
- **Description**: Verify graceful handling of database errors
- **Steps**:
  1. Simulate database connectivity issues
  2. Switch month tabs
- **Expected**: 
  - Error handled gracefully
  - User-friendly error message
  - App doesn't crash

#### Test Case 8.2: Network Error Recovery
- **Description**: Verify recovery from temporary network issues
- **Steps**:
  1. Simulate network interruption during data loading
  2. Restore network and retry
- **Expected**: 
  - Automatic retry or manual refresh option
  - Data loads successfully after network restore

## Test Results Checklist

- [ ] All month tabs generate correctly
- [ ] Tab selection works properly
- [ ] Month filtering returns correct data
- [ ] Wallet integration functions properly
- [ ] Data calculations are accurate
- [ ] Performance is acceptable
- [ ] Edge cases handled correctly
- [ ] UI/UX is smooth and intuitive
- [ ] Error handling is robust

## Known Issues & Limitations

### Current Limitations:
1. Month generation uses LocalDate (requires API level 26+)
2. 18-month history limit (configurable)
3. Tab scrolling requires manual scroll on smaller screens

### Potential Future Improvements:
1. Infinite scroll for historical months
2. Quick date picker for jumping to specific months
3. Month-wise budget integration
4. Export functionality per month

## Testing Environment Requirements

- **Minimum Android API**: 26 (for LocalDate usage)
- **Test Data**: At least 6 months of transaction history
- **Wallets**: Multiple wallets with different currencies
- **Device Types**: Phone and tablet testing recommended 