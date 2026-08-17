package com.example.thesstransit.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.network.JourneyOption
import com.example.thesstransit.ui.network.JourneyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface JourneySearchState {

    data object Idle : JourneySearchState

    data object Loading : JourneySearchState

    data class Success(
        val options: List<JourneyOption>
    ) : JourneySearchState

    data class Error(
        val message: String
    ) : JourneySearchState
}

class JourneySearchViewModel(
    private val repository: JourneyRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<JourneySearchState>(
            JourneySearchState.Idle
        )

    val state: StateFlow<JourneySearchState> =
        _state.asStateFlow()

    fun search(
        originLat: Double,
        originLon: Double,
        destLat: Double,
        destLon: Double,
        departTime: String,
        optimizeFor: String = "arrival_time",
        walkPenalty: Double = 1.0,
        maxTransfers: Int = 3
    ) {

        viewModelScope.launch {

            _state.value =
                JourneySearchState.Loading

            repository.findJourneys(
                originLat = originLat,
                originLon = originLon,
                destLat = destLat,
                destLon = destLon,
                departTime = departTime,
                optimizeFor = optimizeFor,
                walkPenalty = walkPenalty,
                maxTransfers = maxTransfers
            ).fold(
                onSuccess = { options ->
                    _state.value =
                        if (options.isEmpty()) {
                            JourneySearchState.Error(
                                "Δεν βρέθηκε διαθέσιμη διαδρομή"
                            )
                        } else {
                            JourneySearchState.Success(
                                options
                            )
                        }
                },

                onFailure = { error ->
                    _state.value =
                        JourneySearchState.Error(
                            error.message ?: "Κάτι πήγε στραβά."
                        )
                }
            )
        }
    }

    fun reset() {
        _state.value = JourneySearchState.Idle
    }
}

class JourneySearchViewModelFactory(
    private val repository: JourneyRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                JourneySearchViewModel::class.java
            )
        ) {
            return JourneySearchViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}