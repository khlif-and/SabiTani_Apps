package tech.sabitani.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tech.sabitani.feature.home.domain.usecase.ObserveDashboardSummaryUseCase
import tech.sabitani.feature.home.presentation.state.HomeEffect
import tech.sabitani.feature.home.presentation.state.HomeIntent
import tech.sabitani.feature.home.presentation.state.HomeState
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel
    @Inject
    constructor(
        private val observeDashboardSummaryUseCase: ObserveDashboardSummaryUseCase,
    ) : ViewModel(),
        ContainerHost<HomeState, HomeEffect> {
        override val container =
            container<HomeState, HomeEffect>(HomeState()) {
                observeDashboardSummaryUseCase()
                    .onEach { summary ->
                        intent {
                            reduce {
                                state.copy(
                                    isLoading = false,
                                    summary = summary,
                                    errorMessage = null,
                                )
                            }
                        }
                    }.catch { error ->
                        intent {
                            reduce {
                                state.copy(
                                    isLoading = false,
                                    errorMessage = error.message ?: "Gagal memuat ringkasan dashboard.",
                                )
                            }
                        }
                    }.launchIn(viewModelScope)
            }

        fun onIntent(intent: HomeIntent) {
            when (intent) {
                HomeIntent.Refresh -> refresh()
                HomeIntent.OpenFarms -> openFarms()
                HomeIntent.OpenCycles -> openCycles()
            }
        }

        private fun refresh() =
            intent {
                reduce { state.copy(isLoading = true, errorMessage = null) }
            }

        private fun openFarms() =
            intent {
                postSideEffect(HomeEffect.NavigateToFarms)
            }

        private fun openCycles() =
            intent {
                postSideEffect(HomeEffect.NavigateToCycles)
            }
    }
