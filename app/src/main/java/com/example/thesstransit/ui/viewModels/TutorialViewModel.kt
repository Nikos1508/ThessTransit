package com.example.thesstransit.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesstransit.ui.data.TutorialPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TutorialViewModel(
    private val preferences: TutorialPreferences
): ViewModel() {

    val tutorialCompleted =
        preferences.tutorialCompleted
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
        )

    fun completeTutorial() {
        viewModelScope.launch {
            preferences.setTutorialCompleted(true)
        }
    }

    fun replayTutorial() {
        viewModelScope.launch {
            preferences.setTutorialCompleted(false)
        }
    }

}