package com.example.bookstats.features.creation

sealed class BookCreationUiState(open val saveButtonEnabled : Boolean){
    data class Success(
        val bookName: String? = null,
        val bookAuthor: String? = null,
        val numberOfPages: Int = 0,
        override val saveButtonEnabled: Boolean = false
    ) : BookCreationUiState(saveButtonEnabled)
    data class Error(val reason: Reason) : BookCreationUiState(false) {
        sealed class Reason {
            object MissingBookName : Reason()
            object MissingAuthor : Reason()
            object NoPages : Reason()
            class Unknown(val exception: Exception) : Reason()
        }
    }

    object Init : BookCreationUiState(false)

    object Done : BookCreationUiState(true)
}
