package com.example.bookstats.features.creation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
    private var bookImageBitmap: Bitmap? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigationView()
        notifyVmIfBookIsEdited()
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
            handleError(error)
        }
        binding.run {
            bookWithSessions?.let {
                bookImage.load(it.image)
                bookNameEditText.setText(it.name)
                bookAuthorEditText.setText(it.author)
                bookPageNumberEditText.setText(it.totalPages.toString())
                startingPageEditText.setText(it.startingPage.toString())
                updateEditTextAfterImportingBook = false
            }
        }
    }

    private fun setupImagePicker() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        getBitmap(uri.toString())?.let {
                            bookImageBitmap = it
                            binding.bookImage.load(it)
                        }
                    }
                }
            }
        binding.bookImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun handleError(error: Error) {
        with(binding) {
            error.reasons.forEach { reason ->
                when (reason) {
                    Error.Reason.MissingAuthor -> bookAuthor.error =
                        getString(R.string.creation_error_author)
                    Error.Reason.MissingBookName -> bookName.error =
                        getString(R.string.creation_error_name)
                    Error.Reason.NoPages -> pageNumber.error =
                        getString(R.string.creation_error_pages)
                    Error.Reason.MissingImage -> requireContext().showShortToast(R.string.creation_error_image)
                    is Error.Reason.Unknown -> {
                        requireContext().showShortToast(reason.exception.toString())
                    }
                }
            }
        }
    }

    private fun setupScannerButton() {
        binding.scanIsbnButton.setOnClickListener {
            val scanIntent = Intent(requireContext(), ScannerActivity::class.java)
            scanActivityResultLauncher().launch(scanIntent)
        }
    }

    private fun setupSaveBookButtonListener() = binding.run {
        if (isBookBeingEdited()) {
            saveBookButton.text = resources.getString(R.string.edit_book)
        }
        saveBookButton.setOnClickListener {
            viewModel.createOrEditBookIfRequirementsMet(
                BookCreationViewDetails(
                    author = bookAuthorEditText.getString(),
                    name = bookNameEditText.getString(),
                    totalPages = bookPageNumberEditText.getString()?.toInt(),
                    startingPage = startingPageEditText.getString()?.toInt(),
                    image = bookImageBitmap
                )
            )
        }
    }

    private fun notifyVmIfBookIsEdited() {
        arguments?.getLong(EDITED_BOOK_ID)?.let { viewModel.notifyBookIsEdited(it) }
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
                            getBitmap(it)!!
                        })
                }
            }
        }

    private fun isBookBeingEdited(): Boolean = editedBookId != null

    companion object {
        const val ISBN = "ISBN"
        const val EDITED_BOOK_ID = "editedBookId"
    }
}