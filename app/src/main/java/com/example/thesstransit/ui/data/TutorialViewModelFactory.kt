package com.example.thesstransit.ui.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thesstransit.ui.viewModels.TutorialViewModel

class TutorialViewModelFactory(
    private val preferences: TutorialPreferences
): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(TutorialViewModel::class.java) ) {
            return TutorialViewModel(
                preferences
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel"
        )
    }
}