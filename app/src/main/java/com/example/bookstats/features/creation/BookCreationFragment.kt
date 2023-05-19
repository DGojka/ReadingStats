package com.example.bookstats.features.creation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.bookstats.R
import com.example.bookstats.app.ReadingStatsApp
import com.example.bookstats.databinding.FragmentBookCreationBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookCreationFragment : Fragment() {
    private var _binding: FragmentBookCreationBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: BookCreationViewModelFactory
    private lateinit var viewModel: BookCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as ReadingStatsApp).appComponent.inject(this)
        _binding = FragmentBookCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[BookCreationViewModel::class.java]
        initEditTextListeners()
        initSaveBookButtonListener()
        observeState()
        initImagePicker()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                with(state) {
                    if (bookCreated) {
                        findNavController().navigate(R.id.libraryFragment)
                    }
                    if (error != null) {
                        handleError(error.reason)
                    }
                    binding.saveBookButton.isEnabled = state.saveButtonEnabled
                }
            }
        }
    }

    private fun initImagePicker() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(this)
                        .load(Uri.parse(uri.toString()))
                        .into(binding.bookImage)
                }
            }
        binding.bookImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun handleError(reason: Error.Reason) {
        val errorMessageResId = when (reason) {
            Error.Reason.MissingAuthor -> R.string.creation_error_author
            Error.Reason.MissingBookName -> R.string.creation_error_name
            Error.Reason.NoPages -> R.string.creation_error_pages
            is Error.Reason.Unknown -> {
                Log.e(CREATION_ERROR, reason.exception.toString())
                R.string.unknown_error
            }
        }
        Log.e(CREATION_ERROR, reason.toString())
        binding.let {
            Toast.makeText(it.root.context, errorMessageResId, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initEditTextListeners() {
        binding.bookAuthorEditText.addTextChangedListener {
            viewModel.setBookAuthor(it.toString())
        }
        binding.bookNameEditText.addTextChangedListener {
            viewModel.setBookName(it.toString())
        }
        binding.bookPageNumberEditText.addTextChangedListener {
            viewModel.setNumberOfPages(if (it.toString().isNotEmpty()) it.toString().toInt() else 0)
        }
    }

    private fun initSaveBookButtonListener() {
        binding.saveBookButton.setOnClickListener {
            viewModel.createBook()
        }
    }

    companion object {
        const val CREATION_ERROR = "creation_error"
    }
}