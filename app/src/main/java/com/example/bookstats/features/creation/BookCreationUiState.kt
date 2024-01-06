package com.example.bookstats.features.creation

import android.graphics.Bitmap

data class BookCreationUiState(
    val bookWithSessions: BookCreationViewDetails? = null,
    val saveButtonEnabled: Boolean = false,
    val error: Error? = null,
    val isBookCreated: Boolean = false
)

data class BookCreationViewDetails(
    var id: Long = 0,
    val name: String?,
    val author: String?,
    val image: Bitmap?,
    val totalPages: Int?,
    val startingPage: Int? = 0,
)

data class Error(val reasons: List<Reason>) {
    sealed class Reason {
        object MissingBookName : Reason()
        object MissingAuthor : Reason()
        object NoPages : Reason()
        object MissingImage : Reason()
        class Unknown(val exception: Exception) : Reason()
    }
}