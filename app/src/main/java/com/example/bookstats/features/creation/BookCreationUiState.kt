package com.example.bookstats.features.creation

data class BookCreationUiState(
    val bookName: String = "",
    val bookAuthor: String = "",
    val numberOfPages: Int = 0,
    val saveButtonEnabled: Boolean = false,
    val error: Error? = null,
    val bookCreated: Boolean = false
)

data class Error(val reason: Reason) {
    sealed class Reason {
        object MissingBookName : Reason()
        object MissingAuthor : Reason()
        object NoPages : Reason()
        class Unknown(val exception: Exception) : Reason()
    }
}