package com.example.bookstats.features.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.bookstats.R
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.FragmentLibraryBinding
import com.example.bookstats.features.library.list.BookAdapter
import com.example.bookstats.features.library.viewmodel.LibraryViewModel
import com.example.bookstats.features.library.viewmodel.LibraryViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter

    @Inject
    lateinit var viewModelFactory: LibraryViewModelFactory
    private lateinit var viewModel: LibraryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[LibraryViewModel::class.java]
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBookAdapter()
        initCreateBookButton()
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                with(state) {
                    bookAdapter.setData(bookList)
                    binding.apply {
                        currentStreakValue.text = currentStreak.toString()
                        if (lastBook != null) {
                            lastBookContainer.visibility = View.VISIBLE
                            lastBookTextView.visibility = View.VISIBLE
                            lastBookItem.bookImage.load(lastBook.image)
                            lastBookContainer.setOnClickListener {
                                viewModel.initBookMoreDetails(
                                    id = lastBook.id.toInt(),
                                    onInitialized = { findNavController().navigate(R.id.action_libraryFragment_to_more_details) })
                            }
                        }
                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchBooksFromDb()
    }

    private fun initBookAdapter() {
        bookAdapter = BookAdapter(onBookClick = {
            viewModel.initBookMoreDetails(
                id = it,
                onInitialized = { findNavController().navigate(R.id.action_libraryFragment_to_more_details) })
        })
        binding.bookGrid.adapter = bookAdapter
    }

    private fun initCreateBookButton() {
        binding.buttonAddBook.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_bookCreationFragment)
        }
    }
}


