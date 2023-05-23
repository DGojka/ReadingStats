package com.example.bookstats.features.library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.bookstats.R
import com.example.bookstats.activity.MainActivity
import com.example.bookstats.app.ReadingStatsApp
import com.example.bookstats.databinding.FragmentLibraryBinding
import com.example.bookstats.features.library.list.BookAdapter
import com.example.bookstats.features.library.list.BookItem
import com.example.bookstats.features.library.viewmodel.LibraryViewModel
import com.example.bookstats.features.library.viewmodel.LibraryViewModelFactory
import com.example.bookstats.repository.BookWithSessions
import kotlinx.coroutines.launch
import javax.inject.Inject

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter

    @Inject
    lateinit var viewModelFactory: LibraryViewModelFactory
    private val viewModel by viewModels<LibraryViewModel>({ activity as MainActivity }) { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as ReadingStatsApp).appComponent.inject(this)
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        bookAdapter = BookAdapter(onBookClick = {
            viewModel.moreDetails(
                id = it,
                navigate = { findNavController().navigate(R.id.action_libraryFragment_to_more_details) })
        })
        binding.buttonAddBook.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_bookCreationFragment)
        }
        binding.bookGrid.adapter = bookAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                with(state) {
                    bookAdapter.setData(bookList.mapToBookItem())
                    binding.apply {
                        currentStreakValue.text = currentStreak.toString()
                        if (lastBook != null) {
                            lastBookContainer.visibility = View.VISIBLE
                            lastBookTextView.visibility = View.VISIBLE
                            lastBookItem.bookImage.load(lastBook.image)
                            lastBookContainer.setOnClickListener {
                                viewModel.moreDetails(
                                    id = lastBook.id.toInt(),
                                    navigate = { findNavController().navigate(R.id.action_libraryFragment_to_more_details) })
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


    //TEMPORARY METHOD: see BookItem.class
    private fun List<BookWithSessions>.mapToBookItem(): List<BookItem> {
        return this.map {
            BookItem(it.id.toInt(), R.drawable.image_place_holder, it.name, it.image)
        }
    }
}


