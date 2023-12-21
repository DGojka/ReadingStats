package com.example.bookstats.features.bookdetails.viewmodel.uistate

import com.example.bookstats.features.bookdetails.managers.helpers.DialogDetails
import com.example.bookstats.features.bookdetails.tabs.general.helpers.GeneralBookInfo
import com.example.bookstats.features.bookdetails.tabs.sessions.helpers.SessionDetails
import com.example.bookstats.repository.BookWithSessions

data class BookDetailsUiState(
    val book: BookWithSessions?,
    val dialogDetails: DialogDetails?,
    val bookInfo: GeneralBookInfo?,
    val sessionDetails: List<SessionDetails>?,
    val bookPercentage: Int = 0
)