package com.example.bookstats.features.library.viewmodel.uistate

import com.example.bookstats.features.library.managers.helpers.DialogDetails
import com.example.bookstats.repository.BookWithSessions

data class LibraryUiState(
    val isLoading: Boolean,
    val bookList: List<BookWithSessions> = mutableListOf(),
    val bookClicked: BookWithSessions? = null,
    val lastBook: BookWithSessions?,
    val dialogDetails: DialogDetails? = null
)

