package com.example.bookstats.features.realtimesessions.viewmodel

data class RealTimeSessionsUiState(
    val currentMs: Float,
    val error: Error? = null
)

data class Error(val reason: Reason) {
    sealed class Reason {
        class NewPageIsLowerThanOld(val currentPage: Int) : Reason()
        object NewPageIsGreaterThanTotalBookPages : Reason()
    }
}