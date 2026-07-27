package com.example.thesstransit.ui.data

enum class MetroBranch {
    MAIN,
    MIKRA,
    NEA_ELVETIA
}

data class MetroStop(
    val id: Int,
    val mainName: String,
    val secName: String,
    val branch: MetroBranch,

    val travelSeconds: Int = 1
)