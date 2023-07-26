package com.example.bookstats.features.realtimesessions.viewmodel

import com.example.bookstats.repository.Session

data class RealTimeSessionsUiState(
    val currentMs: Float,
    val error: Error? = null,
    val session: Session? = null
)

data class Error(val reason: Reason) {
    sealed class Reason {
        class NewPageIsLowerThanOld(val currentPage: Int) : Reason()
        object NewPageIsGreaterThanTotalBookPages : Reason()
    }
}