package com.example.bookstats.features.creation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.bookstats.R
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.FragmentBookCreationBinding
import com.example.bookstats.extensions.*
import com.example.bookstats.features.scanner.ScannerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class BookCreationFragment : Fragment(R.layout.fragment_book_creation) {

    @Inject
    lateinit var viewModelProvider: Provider<BookCreationViewModel>

    private val binding by viewBinding(FragmentBookCreationBinding::bind)
    private val viewModel by daggerViewModel { viewModelProvider }

    private var updateEditTextAfterImportingBook = false
    private var editedBookId: Long? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigationView()
        notifyVmIfBookIsEdited()
        setupEditTextListeners()
        setupSaveBookButtonListener()
        observeState()
        setupImagePicker()
        setupScannerButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigationView()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.setupViews()
            }
        }
    }

    private fun BookCreationUiState.setupViews() {
        if (isBookCreated) {
            findNavController().navigate(R.id.libraryFragment)
        }
        if (error != null) {
            handleError(error.reason)
        }
        binding.let {
            it.saveBookButton.isEnabled = saveButtonEnabled
            if (image != null) {
                it.bookImage.load(image)
            }
            if (updateEditTextAfterImportingBook) {
                it.bookNameEditText.setText(bookName)
                it.bookAuthorEditText.setText(bookAuthor)
                it.bookPageNumberEditText.setText(numberOfPages.toString())
                it.startingPageEditText.setText(startingPage.toString())
                updateEditTextAfterImportingBook = false
            }
        }
    }

    private fun setupImagePicker() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val bitmap = getBitmap(uri.toString())
                        viewModel.setImageBitmap(bitmap!!)
                    }
                }
            }
        binding.bookImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun handleError(reason: Error.Reason) {
        val errorMessage = when (reason) {
            Error.Reason.MissingAuthor -> getString(R.string.creation_error_author)
            Error.Reason.MissingBookName -> getString(R.string.creation_error_name)
            Error.Reason.NoPages -> getString(R.string.creation_error_pages)
            is Error.Reason.Unknown -> {
                getString(R.string.unknown_error)
            }
        }
        Log.e(CREATION_ERROR, reason.toString())
        requireContext().showShortToast(errorMessage)
    }

    private fun setupEditTextListeners() = binding.run {
        bookAuthorEditText.addTextChangedListener {
            viewModel.setBookAuthor(it.toString())
        }
        bookNameEditText.addTextChangedListener {
            viewModel.setBookName(it.toString())
        }
        bookPageNumberEditText.addTextChangedListener {
            viewModel.setNumberOfPages(if (it.toString().isNotEmpty()) it.toString().toInt() else 0)
        }
        startingPageEditText.addTextChangedListener {
            viewModel.setStartingPage(if (it.toString().isNotEmpty()) it.toString().toInt() else 0)
        }
    }

    private fun setupScannerButton() {
        binding.scanIsbnButton.setOnClickListener {
            val scanIntent = Intent(requireContext(), ScannerActivity::class.java)
            scanActivityResultLauncher().launch(scanIntent)
        }
    }

    private fun setupSaveBookButtonListener() {
        if (isBookBeingEdited()) {
            binding.saveBookButton.text = resources.getString(R.string.edit_book)
        }
        binding.saveBookButton.setOnClickListener {
            if (isBookBeingEdited()) {
                viewModel.saveChanges(bookId = editedBookId!!)
            } else {
                viewModel.createBook()
            }

        }
    }

    private fun notifyVmIfBookIsEdited() {
        editedBookId = arguments?.getLong(EDITED_BOOK_ID)
        if (editedBookId != null) {
            viewModel.editBook(editedBookId!!) {
                updateEditTextAfterImportingBook = true
            }
        }
    }

    private suspend fun getBitmap(uri: String): Bitmap? {
        val loader = ImageLoader(requireContext())
        val request = ImageRequest.Builder(requireContext()).data(uri).build()

        val result = (loader.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    private fun scanActivityResultLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val isbn = data?.getStringExtra(ISBN)
                isbn?.let {
                    viewModel.importBookByISBN(
                        isbn,
                        onResponseGetBitmap = {
                            updateEditTextAfterImportingBook = true
                            getBitmap(it)!!
                        })
                }
            }
        }

    private fun isBookBeingEdited(): Boolean = editedBookId != null

    companion object {
        const val CREATION_ERROR = "creation_error"
        const val ISBN = "ISBN"
        const val EDITED_BOOK_ID = "editedBookId"
    }
}