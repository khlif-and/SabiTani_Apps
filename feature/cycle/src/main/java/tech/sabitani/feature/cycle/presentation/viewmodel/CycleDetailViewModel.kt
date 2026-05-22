package tech.sabitani.feature.cycle.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tech.sabitani.feature.cycle.domain.usecase.AddActivityUseCase
import tech.sabitani.feature.cycle.domain.usecase.AddTransactionUseCase
import tech.sabitani.feature.cycle.domain.usecase.ObserveActivitiesUseCase
import tech.sabitani.feature.cycle.domain.usecase.ObserveCostSummaryUseCase
import tech.sabitani.feature.cycle.domain.usecase.ObserveCycleDetailUseCase
import tech.sabitani.feature.cycle.domain.usecase.ObserveTransactionsUseCase
import tech.sabitani.feature.cycle.presentation.screen.CycleDetailRoute
import tech.sabitani.feature.cycle.presentation.state.ActivityDraft
import tech.sabitani.feature.cycle.presentation.state.ActivityIntent
import tech.sabitani.feature.cycle.presentation.state.CycleDetailEffect
import tech.sabitani.feature.cycle.presentation.state.CycleDetailIntent
import tech.sabitani.feature.cycle.presentation.state.CycleDetailState
import tech.sabitani.feature.cycle.presentation.state.TransactionDraft
import tech.sabitani.feature.cycle.presentation.state.TransactionIntent
import javax.inject.Inject

@HiltViewModel
internal class CycleDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        observeCycleDetailUseCase: ObserveCycleDetailUseCase,
        observeActivitiesUseCase: ObserveActivitiesUseCase,
        observeTransactionsUseCase: ObserveTransactionsUseCase,
        observeCostSummaryUseCase: ObserveCostSummaryUseCase,
        private val addActivityUseCase: AddActivityUseCase,
        private val addTransactionUseCase: AddTransactionUseCase,
        private val clock: Clock,
    ) : ViewModel(),
        ContainerHost<CycleDetailState, CycleDetailEffect> {
        private val route = savedStateHandle.toRoute<CycleDetailRoute>()

        override val container =
            container<CycleDetailState, CycleDetailEffect>(
                CycleDetailState(cycleId = route.cycleId),
            ) {
                combine(
                    observeCycleDetailUseCase(route.cycleId),
                    observeActivitiesUseCase(route.cycleId),
                    observeTransactionsUseCase(route.cycleId),
                    observeCostSummaryUseCase(route.cycleId),
                ) { cycle, activities, transactions, summary ->
                    intent {
                        reduce {
                            state.copy(
                                isLoading = false,
                                cycle = cycle,
                                activities = activities,
                                transactions = transactions,
                                costSummary = summary,
                            )
                        }
                    }
                }.launchIn(viewModelScope)
            }

        fun onIntent(action: CycleDetailIntent) {
            when (action) {
                is CycleDetailIntent.TabSelected -> reduceState { it.copy(selectedTab = action.tab) }
                is ActivityIntent -> handleActivityIntent(action)
                is TransactionIntent -> handleTransactionIntent(action)
            }
        }

        private fun handleActivityIntent(action: ActivityIntent) {
            when (action) {
                ActivityIntent.OpenActivityDialog -> openActivityDialog()
                ActivityIntent.DismissActivityDialog ->
                    reduceState { it.copy(activityDraft = null) }
                is ActivityIntent.ActivityTypeChanged ->
                    updateActivity { it.copy(type = action.value) }
                is ActivityIntent.ActivityDateChanged ->
                    updateActivity { it.copy(performedOn = action.value) }
                is ActivityIntent.ActivityMaterialChanged ->
                    updateActivity { it.copy(material = action.value) }
                is ActivityIntent.ActivityDosageChanged ->
                    updateActivity { it.copy(dosage = action.value) }
                is ActivityIntent.ActivityNotesChanged ->
                    updateActivity { it.copy(notes = action.value) }
                ActivityIntent.SubmitActivity -> submitActivity()
            }
        }

        private fun handleTransactionIntent(action: TransactionIntent) {
            when (action) {
                TransactionIntent.OpenTransactionDialog -> openTransactionDialog()
                TransactionIntent.DismissTransactionDialog ->
                    reduceState { it.copy(transactionDraft = null) }
                is TransactionIntent.TransactionCategoryChanged ->
                    updateTransaction { it.copy(category = action.value) }
                is TransactionIntent.TransactionAmountChanged ->
                    updateTransaction { it.copy(amountText = action.value) }
                is TransactionIntent.TransactionDateChanged ->
                    updateTransaction { it.copy(occurredOn = action.value) }
                is TransactionIntent.TransactionNotesChanged ->
                    updateTransaction { it.copy(notes = action.value) }
                TransactionIntent.SubmitTransaction -> submitTransaction()
            }
        }

        private fun today() = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        private fun reduceState(block: (CycleDetailState) -> CycleDetailState) =
            intent {
                reduce { block(state) }
            }

        private fun openActivityDialog() =
            reduceState {
                it.copy(activityDraft = ActivityDraft(performedOn = today()))
            }

        private fun openTransactionDialog() =
            reduceState {
                it.copy(transactionDraft = TransactionDraft(occurredOn = today()))
            }

        private fun updateActivity(block: (ActivityDraft) -> ActivityDraft) =
            intent {
                state.activityDraft?.let { current -> reduce { state.copy(activityDraft = block(current)) } }
            }

        private fun updateTransaction(block: (TransactionDraft) -> TransactionDraft) =
            intent {
                state.transactionDraft?.let { current ->
                    reduce { state.copy(transactionDraft = block(current)) }
                }
            }

        private fun submitActivity() =
            intent {
                val draft = state.activityDraft ?: return@intent
                val performedOn = draft.performedOn
                if (performedOn == null) {
                    postSideEffect(CycleDetailEffect.ShowError("Tanggal aktivitas wajib diisi."))
                    return@intent
                }
                reduce { state.copy(activityDraft = draft.copy(isSubmitting = true)) }
                val result =
                    addActivityUseCase(
                        cycleId = state.cycleId,
                        type = draft.type,
                        performedOn = performedOn,
                        material = draft.material,
                        dosage = draft.dosage,
                        notes = draft.notes,
                    )
                result.fold(
                    onSuccess = { reduce { state.copy(activityDraft = null) } },
                    onFailure = { error ->
                        reduce { state.copy(activityDraft = draft.copy(isSubmitting = false)) }
                        postSideEffect(CycleDetailEffect.ShowError(error.message ?: "Gagal menyimpan aktivitas."))
                    },
                )
            }

        private fun submitTransaction() =
            intent {
                val draft = state.transactionDraft ?: return@intent
                val occurredOn = draft.occurredOn
                val amount =
                    draft.amountText
                        .replace(".", "")
                        .replace(",", "")
                        .toLongOrNull()
                if (occurredOn == null) {
                    postSideEffect(CycleDetailEffect.ShowError("Tanggal transaksi wajib diisi."))
                    return@intent
                }
                if (amount == null || amount <= 0L) {
                    postSideEffect(CycleDetailEffect.ShowError("Nominal harus angka lebih dari 0."))
                    return@intent
                }
                reduce { state.copy(transactionDraft = draft.copy(isSubmitting = true)) }
                val result =
                    addTransactionUseCase(
                        cycleId = state.cycleId,
                        category = draft.category,
                        amountIdr = amount,
                        occurredOn = occurredOn,
                        notes = draft.notes,
                    )
                result.fold(
                    onSuccess = { reduce { state.copy(transactionDraft = null) } },
                    onFailure = { error ->
                        reduce { state.copy(transactionDraft = draft.copy(isSubmitting = false)) }
                        postSideEffect(CycleDetailEffect.ShowError(error.message ?: "Gagal menyimpan transaksi."))
                    },
                )
            }
    }
