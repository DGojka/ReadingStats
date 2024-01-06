package com.example.bookstats.features.creation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.repository.BookWithSessions
import com.example.bookstats.repository.Repository
import com.example.bookstats.repository.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class BookCreationViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _uiState = MutableStateFlow(BookCreationUiState())
    val uiState: StateFlow<BookCreationUiState> = _uiState

    private var editedBookId: Long? = null

    private val Any.logTag: String
        get() {
            val tag = javaClass.simpleName
            return tag.take(23)
        }

    fun createOrEditBookIfRequirementsMet(
        book: BookCreationViewDetails
    ) {
        viewModelScope.launch {
            val errorReasons = mutableListOf<Error.Reason>()
            with(book) {
                if (author.isNullOrBlank()) errorReasons.add(Error.Reason.MissingAuthor)
                if (name.isNullOrBlank()) errorReasons.add(Error.Reason.MissingBookName)
                if (totalPages.isNullOrZero()) errorReasons.add(Error.Reason.NoPages)
                if (image == null) errorReasons.add(Error.Reason.MissingImage)

                if (errorReasons.isEmpty()) {
                    try {
                        editedBookId?.let { saveChanges(it, book) } ?: saveBook(book)
                    } catch (ex: Exception) {
                        errorReasons.add(Error.Reason.Unknown(ex))
                        emitError(errorReasons)
                    }
                } else {
                    emitError(errorReasons)
                }
            }

        }
    }

    private fun saveBook(
        book: BookCreationViewDetails
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            with(_uiState.value) {
                repository.addBookWithSessions(book.toBookWithSessions())
                Log.i(logTag, "Saved new book: ${book.name}")
                _uiState.value = copy(isBookCreated = true)
            }
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val stream = ByteArrayOutputStream()
        var currQuality = 100
        val scaledBitmap = scaleBitmap(bitmap)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, currQuality, stream)
        var currSize = stream.toByteArray().size

        while (currSize >= MAX_BITMAP_SIZE_BYTES) {
            stream.reset()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, currQuality, stream)
            currSize = stream.toByteArray().size
            currQuality -= 1
        }

        val compressedBytes = stream.toByteArray()
        return BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap =
        Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true)

    private fun emitError(reasons: List<Error.Reason>) {
        _uiState.value =
            _uiState.value.copy(error = Error(reasons))
    }

    fun importBookByISBN(isbn: String, onResponseGetBitmap: suspend (url: String) -> Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val bookVolumeInfo = repository.getBookByISBN(isbn)
            with(bookVolumeInfo) {
                if (this != null) {
                    var bitmap = onResponseGetBitmap(getImageUrl(isbn))
                    if (bitmap.width <= 1 && bitmap.height <= 1) {
                        if (imageLinks != null) {
                            bitmap =
                                onResponseGetBitmap(imageLinks.thumbnail!!.replace("http", "https"))
                        } else {
                            //TODO: implement placeholder
                        }
                    }
                    val book = BookWithSessions(
                        author = authors[0],
                        name = title,
                        totalPages = pageCount,
                        image = bitmap,
                        currentPage = 0,
                        startingPage = 0,
                        filters = mutableListOf(),
                        sessions = mutableListOf()
                    )
                    _uiState.value =
                        _uiState.value.copy(bookWithSessions = book.toBookCreationView())
                }
            }
        }
    }

    private fun getImageUrl(isbn: String) =
        "https://covers.openlibrary.org/b/isbn/$isbn-L.jpg"

    fun notifyBookIsEdited(editedBookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val editedBook = repository.getBookWithSessionsById(editedBookId)
            _uiState.value = _uiState.value.copy(bookWithSessions = editedBook.toBookCreationView())
        }
    }

    private fun saveChanges(bookId: Long, book: BookCreationViewDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            val bookWithSessions = repository.getBookWithSessionsById(bookId)
            with(bookWithSessions) {
                repository.editBookWithSessions(
                    bookId,
                    book.toBookWithSessions(currentPage, filters, sessions)
                )
            }

            Log.i(logTag, "Edited book with id: ${bookWithSessions.id}, ${bookWithSessions.name}")
            _uiState.value = _uiState.value.copy(isBookCreated = true)
        }
    }

    private fun BookCreationViewDetails.toBookWithSessions(
        currentPage: Int = 0,
        filters: List<String> = mutableListOf(),
        sessions: List<Session> = mutableListOf()
    ) = BookWithSessions(
        name!!,
        author!!,
        compressBitmap(image!!),
        totalPages!!,
        startingPage!!,
        currentPage,
        filters,
        sessions
    )

    private fun BookWithSessions.toBookCreationView(): BookCreationViewDetails {
        return BookCreationViewDetails(id, name, author, image, totalPages, startingPage)
    }

    private fun Int?.isNullOrZero() = this == null || this == 0

    companion object {
        private const val IMAGE_HEIGHT = 1080
        private const val IMAGE_WIDTH = 720
        private const val MAX_BITMAP_SIZE_BYTES = 4194304
    }

}