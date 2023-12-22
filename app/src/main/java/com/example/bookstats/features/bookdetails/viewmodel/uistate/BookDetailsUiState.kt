package com.example.bookstats.features.bookdetails.viewmodel.uistate

import com.example.bookstats.features.bookdetails.tabs.general.helpers.GeneralBookInfo
import com.example.bookstats.features.bookdetails.tabs.sessions.helpers.SessionDetails
import com.example.bookstats.repository.BookWithSessions

data class BookDetailsUiState(
    val book: BookWithSessions? = null,
    val bookInfo: GeneralBookInfo? = null,
    val sessionDetails: List<SessionDetails>? = null,
    val bookPercentage: Int = 0
)