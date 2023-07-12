package com.example.bookstats.features.bookdetails.viewmodel.uistate

import com.example.bookstats.features.bookdetails.managers.helpers.DialogDetails
import com.example.bookstats.repository.BookWithSessions

data class BookDetailsUiState(val book: BookWithSessions?, val dialogDetails: DialogDetails?)