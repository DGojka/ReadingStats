package com.example.bookstats.features.library.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.bookstats.R
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.FragmentLibraryBinding
import com.example.bookstats.extensions.daggerViewModel
import com.example.bookstats.extensions.viewBinding
import com.example.bookstats.extensions.visibleOrInvisible
import com.example.bookstats.features.library.list.BookAdapter
import com.example.bookstats.features.library.viewmodel.LibraryViewModel
import com.example.bookstats.features.library.viewmodel.uistate.LibraryUiState
import com.example.bookstats.repository.BookWithSessions
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class LibraryFragment : Fragment(R.layout.fragment_library) {

    @Inject
    lateinit var viewModelProvider: Provider<LibraryViewModel>

    private val binding by viewBinding(FragmentLibraryBinding::bind)
    private val viewModel by daggerViewModel { viewModelProvider }

    private lateinit var bookAdapter: BookAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBookAdapter()
        setupCreateBookButton()
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.bindViews()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchBooksFromDb()
    }

    private fun setupBookAdapter() {
        bookAdapter = BookAdapter(onBookClick = {
            viewModel.initBookMoreDetails(
                id = it,
                onInitialized = { findNavController().navigate(R.id.action_libraryFragment_to_more_details) })
        })
        binding.bookGrid.adapter = bookAdapter
    }

    private fun setupCreateBookButton() {
        binding.buttonAddBook.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_bookCreationFragment)
        }
    }

    private fun LibraryUiState.bindViews(){
        bookAdapter.submitList(bookList)
        binding.apply {
            currentStreakValue.text = currentStreak.toString()
            lastBook?.let { setupLastBookViews(lastBook) }
        }
    }

    private fun setupLastBookViews(lastBook: BookWithSessions) = binding.run {
        lastBookContainer.visibleOrInvisible(true)
        lastBookTextView.visibleOrInvisible(true)
        lastBookItem.bookImage.load(lastBook.image)
        lastBookContainer.setOnClickListener {
            viewModel.initBookMoreDetails(
                id = lastBook.id.toInt(),
                onInitialized = { findNavController().navigate(R.id.action_libraryFragment_to_more_details) })
        }
    }
}
