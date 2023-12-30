package com.example.bookstats.features.realtimesessions.viewmodel

import com.example.bookstats.features.bookdetails.tabs.sessions.helpers.SessionDetails

data class RealTimeSessionsUiState(
    val currentTime: String,
    val error: Error? = null,
    val sessionDetails: SessionDetails? = null
)

data class Error(val reason: Reason) {
    sealed class Reason {
        class NewPageIsLowerThanOld(val currentPage: Int) : Reason()
        object NewPageIsGreaterThanTotalBookPages : Reason()
    }
}