package com.example.thesstransit.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.thesstransit.ui.data.MetroBranch
import com.example.thesstransit.ui.data.MetroStop

class MetroViewModel : ViewModel() {

    var trainProgress by mutableFloatStateOf(0f)
        private set

    var currentStationIndex by mutableIntStateOf(0)
        private set

    val stations = listOf(

        MetroStop(
            id = 0,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),

        MetroStop(
            id = 1,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),

        MetroStop(
            id = 2,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),
        MetroStop(
            id = 3,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),

        MetroStop(
            id = 4,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),

        MetroStop(
            id = 5,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),

        MetroStop(
            id = 6,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),

        MetroStop(
            id = 7,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),

        MetroStop(
            id = 8,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),

        MetroStop(
            id = 9,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),

        MetroStop(
            id = 10,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MAIN
        ),

        MetroStop(
            id = 11,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MIKRA
        ),

        MetroStop(
            id = 12,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MIKRA
        ),

        MetroStop(
            id = 13,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MIKRA
        ),

        MetroStop(
            id = 14,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MIKRA
        ),

        MetroStop(
            id = 15,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.MIKRA
        ),

        MetroStop(
            id = 16,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.NEA_ELVETIA
        ),

        MetroStop(
            id = 17,
            mainName = "ΝΣ. Σταθμός",
            secName = "New Railway Station",
            branch = MetroBranch.NEA_ELVETIA
        )

    )
}