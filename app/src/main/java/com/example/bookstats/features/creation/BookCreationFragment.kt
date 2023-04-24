package com.example.bookstats.features.creation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.bookstats.R
import com.example.bookstats.app.ReadingStatsApp
import com.example.bookstats.databinding.FragmentBookCreationBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class BookCreationFragment : Fragment() {
    private var _binding: FragmentBookCreationBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var viewModelFactory: BookCreationViewModelFactory
    private lateinit var vm: BookCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as ReadingStatsApp).appComponent.inject(this)
        _binding = FragmentBookCreationBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(this, viewModelFactory)[BookCreationViewModel::class.java]

        binding!!.bookAuthorEditText.addTextChangedListener {
            vm.setBookAuthor(it.toString())
        }
        binding!!.bookNameEditText.addTextChangedListener {
            vm.setBookName(it.toString())
        }
        binding!!.bookPageNumberEditText.addTextChangedListener {
            vm.setNumberOfPages(it.toString())
        }
        binding!!.saveBookButton.setOnClickListener {
            findNavController().navigate(R.id.libraryFragment)
            vm.createBook()
        }
        observeState(viewModel = vm)
    }

    private fun observeState(viewModel: BookCreationViewModel) {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleState(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleState(state: BookCreationUiState) {
        when (state) {
            BookCreationUiState.Done -> {
                findNavController().navigate(R.id.libraryFragment)
            }
            is BookCreationUiState.Error -> {
                handleError(reason = state.reason)
            }
            is BookCreationUiState.Success -> {
                binding!!.saveBookButton.isEnabled = state.saveButtonEnabled
            }
            else -> {}
        }
    }

    private fun handleError(reason: BookCreationUiState.Error.Reason) {
        when (reason) {
            BookCreationUiState.Error.Reason.MissingAuthor -> {
                Log.e(CREATION_ERROR, reason.toString())
                Toast.makeText(requireContext(), R.string.creation_error_author, Toast.LENGTH_SHORT)
                    .show()
            }
            BookCreationUiState.Error.Reason.MissingBookName -> {
                Log.e(CREATION_ERROR, reason.toString())
                Toast.makeText(requireContext(), R.string.creation_error_name, Toast.LENGTH_SHORT)
                    .show()
            }
            BookCreationUiState.Error.Reason.NoPages -> {
                Log.e(CREATION_ERROR, reason.toString())
                Toast.makeText(requireContext(), R.string.creation_error_pages, Toast.LENGTH_SHORT)
                    .show()
            }
            is BookCreationUiState.Error.Reason.Unknown -> {
                Log.e(CREATION_ERROR, reason.exception.toString())
                Toast.makeText(requireContext(), R.string.unknown_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val CREATION_ERROR = "creation_error"
    }
}