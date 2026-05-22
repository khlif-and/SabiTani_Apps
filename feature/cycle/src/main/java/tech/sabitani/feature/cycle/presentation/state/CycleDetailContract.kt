package tech.sabitani.feature.cycle.presentation.state

import kotlinx.datetime.LocalDate
import tech.sabitani.core.model.ActivityType
import tech.sabitani.core.model.CropCycle
import tech.sabitani.core.model.CycleCostSummary
import tech.sabitani.core.model.FarmActivity
import tech.sabitani.core.model.Transaction
import tech.sabitani.core.model.TransactionCategory

enum class CycleDetailTab { ACTIVITIES, TRANSACTIONS }

data class ActivityDraft(
    val type: ActivityType = ActivityType.FERTILIZING,
    val performedOn: LocalDate? = null,
    val material: String = "",
    val dosage: String = "",
    val notes: String = "",
    val isSubmitting: Boolean = false,
)

data class TransactionDraft(
    val category: TransactionCategory = TransactionCategory.FERTILIZER,
    val amountText: String = "",
    val occurredOn: LocalDate? = null,
    val notes: String = "",
    val isSubmitting: Boolean = false,
)

data class CycleDetailState(
    val cycleId: Long = 0L,
    val isLoading: Boolean = true,
    val cycle: CropCycle? = null,
    val activities: List<FarmActivity> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val costSummary: CycleCostSummary? = null,
    val selectedTab: CycleDetailTab = CycleDetailTab.ACTIVITIES,
    val activityDraft: ActivityDraft? = null,
    val transactionDraft: TransactionDraft? = null,
)

sealed interface CycleDetailIntent {
    data class TabSelected(val tab: CycleDetailTab) : CycleDetailIntent
    data object OpenActivityDialog : CycleDetailIntent
    data object DismissActivityDialog : CycleDetailIntent
    data class ActivityTypeChanged(val value: ActivityType) : CycleDetailIntent
    data class ActivityDateChanged(val value: LocalDate) : CycleDetailIntent
    data class ActivityMaterialChanged(val value: String) : CycleDetailIntent
    data class ActivityDosageChanged(val value: String) : CycleDetailIntent
    data class ActivityNotesChanged(val value: String) : CycleDetailIntent
    data object SubmitActivity : CycleDetailIntent

    data object OpenTransactionDialog : CycleDetailIntent
    data object DismissTransactionDialog : CycleDetailIntent
    data class TransactionCategoryChanged(val value: TransactionCategory) : CycleDetailIntent
    data class TransactionAmountChanged(val value: String) : CycleDetailIntent
    data class TransactionDateChanged(val value: LocalDate) : CycleDetailIntent
    data class TransactionNotesChanged(val value: String) : CycleDetailIntent
    data object SubmitTransaction : CycleDetailIntent
}

sealed interface CycleDetailEffect {
    data class ShowError(val message: String) : CycleDetailEffect
}
