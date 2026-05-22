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
    data class TabSelected(
        val tab: CycleDetailTab,
    ) : CycleDetailIntent
}

sealed interface ActivityIntent : CycleDetailIntent {
    data object OpenActivityDialog : ActivityIntent

    data object DismissActivityDialog : ActivityIntent

    data class ActivityTypeChanged(
        val value: ActivityType,
    ) : ActivityIntent

    data class ActivityDateChanged(
        val value: LocalDate,
    ) : ActivityIntent

    data class ActivityMaterialChanged(
        val value: String,
    ) : ActivityIntent

    data class ActivityDosageChanged(
        val value: String,
    ) : ActivityIntent

    data class ActivityNotesChanged(
        val value: String,
    ) : ActivityIntent

    data object SubmitActivity : ActivityIntent
}

sealed interface TransactionIntent : CycleDetailIntent {
    data object OpenTransactionDialog : TransactionIntent

    data object DismissTransactionDialog : TransactionIntent

    data class TransactionCategoryChanged(
        val value: TransactionCategory,
    ) : TransactionIntent

    data class TransactionAmountChanged(
        val value: String,
    ) : TransactionIntent

    data class TransactionDateChanged(
        val value: LocalDate,
    ) : TransactionIntent

    data class TransactionNotesChanged(
        val value: String,
    ) : TransactionIntent

    data object SubmitTransaction : TransactionIntent
}

sealed interface CycleDetailEffect {
    data class ShowError(
        val message: String,
    ) : CycleDetailEffect
}
