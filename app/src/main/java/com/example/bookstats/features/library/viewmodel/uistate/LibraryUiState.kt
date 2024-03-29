package com.example.bookstats.features.library.viewmodel.uistate

import com.example.bookstats.repository.BookWithSessions

data class LibraryUiState(
    val isLoading: Boolean,
    val bookList: List<BookWithSessions>,
    val lastBook: BookWithSessions?,
    val currentStreak: Int
)

