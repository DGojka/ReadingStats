package com.example.bookstats.features.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookstats.R
import com.example.bookstats.databinding.FragmentLibraryBinding
import com.example.bookstats.features.library.list.BookAdapter
import com.example.bookstats.features.library.list.BookItem

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        bookAdapter = BookAdapter()
        binding.bookGrid.adapter = bookAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bookData = listOf(
            BookItem(1, R.drawable.image_place_holder, "book 1"),
            BookItem(2, R.drawable.image_place_holder, "book 2"),
            BookItem(3, R.drawable.image_place_holder, "book 3"),
            BookItem(4, R.drawable.image_place_holder, "book 4"),
        )
        bookAdapter.setData(bookData)
    }
}