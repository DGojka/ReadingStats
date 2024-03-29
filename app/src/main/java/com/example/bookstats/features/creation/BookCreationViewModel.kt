package com.example.bookstats.features.creation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstats.repository.BookWithSessions
import com.example.bookstats.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject


class BookCreationViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _uiState = MutableStateFlow(BookCreationUiState())
    val uiState: StateFlow<BookCreationUiState> = _uiState

    private val Any.logTag: String
        get() {
            val tag = javaClass.simpleName
            return tag.take(23)
        }

    fun setBookName(name: String) {
        val updatedUiState = _uiState.value.copy(bookName = name)
        updateUiState(updatedUiState)
    }

    fun setBookAuthor(author: String) {
        val updatedUiState = _uiState.value.copy(bookAuthor = author)
        updateUiState(updatedUiState)
    }

    fun setNumberOfPages(numberOfPages: Int) {
        val updatedUiState = _uiState.value.copy(numberOfPages = numberOfPages)
        updateUiState(updatedUiState)
    }

    fun setStartingPage(startingPage: Int) {
        val updatedUiState = _uiState.value.copy(startingPage = startingPage)
        updateUiState(updatedUiState)
    }

    fun createBook() {
        viewModelScope.launch {
            with(_uiState.value) {
                when {
                    bookAuthor.isBlank() -> showError(Error.Reason.MissingAuthor)
                    bookName.isBlank() -> showError(Error.Reason.MissingBookName)
                    numberOfPages <= 0 -> showError(Error.Reason.NoPages)
                    else -> try {
                        saveBook()
                    } catch (ex: java.lang.Exception) {
                        _uiState.value = copy(error = Error(Error.Reason.Unknown(ex)))
                    }
                }
            }
        }
    }

    fun setImageBitmap(bitmap: Bitmap) {
        val compressedBitmap = compressBitmap(bitmap)
        val updatedUiState = _uiState.value.copy(image = compressedBitmap)
        updateUiState(updatedUiState)
    }

    private fun saveBook() {
        viewModelScope.launch(Dispatchers.IO) {
            with(_uiState.value) {
                repository.addBookWithSessions(
                    BookWithSessions(
                        bookName,
                        bookAuthor,
                        image!!,
                        numberOfPages,
                        startingPage,
                        startingPage,
                        mutableListOf(),
                        mutableListOf()
                    )
                )
                Log.i(logTag, "Saved new book: $bookName")
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

    private fun showError(reason: Error.Reason) {
        _uiState.value =
            _uiState.value.copy(error = Error(reason))
    }

    private fun updateUiState(updatedUiState: BookCreationUiState) {
        _uiState.value =
            updatedUiState.copy(saveButtonEnabled = isSaveButtonEnabled(updatedUiState))
    }

    private fun isSaveButtonEnabled(state: BookCreationUiState): Boolean {
        with(state) {
            return bookName.isNotBlank() && bookAuthor.isNotBlank() && numberOfPages > 0 && image != null
        }
    }

    fun importBookByISBN(isbn: String, onResponseGetBitmap: suspend (url: String) -> Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = repository.getBookByISBN(isbn)
            with(book) {
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
                    _uiState.value = _uiState.value.copy(
                        bookAuthor = authors[0],
                        bookName = title,
                        numberOfPages = pageCount,
                        image = bitmap,
                        saveButtonEnabled = true
                    )
                }
            }
        }
    }

    private fun getImageUrl(isbn: String) =
        "https://covers.openlibrary.org/b/isbn/$isbn-L.jpg"

    fun editBook(editedBookId: Long, onImportComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val editedBook = repository.getBookWithSessionsById(editedBookId)
            with(editedBook) {
                _uiState.value = _uiState.value.copy(
                    bookName = name,
                    bookAuthor = author,
                    numberOfPages = totalPages,
                    startingPage = startingPage,
                    image = image
                )
            }
            onImportComplete()
        }
    }

    fun saveChanges(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            with(_uiState.value) {
                repository.editBookWithSessions(
                    bookId,
                    BookWithSessions(
                        bookName,
                        bookAuthor,
                        image!!,
                        numberOfPages,
                        startingPage,
                        startingPage,
                        mutableListOf(),
                        mutableListOf()
                    )
                )
                Log.i(logTag, "Saved new book: $bookName")
                _uiState.value = copy(isBookCreated = true)
            }
        }
    }

    companion object {
        private const val IMAGE_HEIGHT = 924
        private const val IMAGE_WIDTH = 640
        private const val MAX_BITMAP_SIZE_BYTES = 4194304
    }

}