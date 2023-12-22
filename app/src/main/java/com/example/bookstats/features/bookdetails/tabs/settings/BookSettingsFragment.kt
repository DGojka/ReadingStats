package com.example.bookstats.features.bookdetails.tabs.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bookstats.R
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.TabSettingsBinding
import com.example.bookstats.extensions.daggerParentActivityViewModel
import com.example.bookstats.extensions.showShortToast
import com.example.bookstats.extensions.viewBinding
import com.example.bookstats.features.bookdetails.viewmodel.BookDetailsViewModel
import com.example.bookstats.features.creation.BookCreationFragment
import javax.inject.Inject
import javax.inject.Provider

class BookSettingsFragment : Fragment(R.layout.tab_settings) {

    @Inject
    lateinit var viewModelProvider: Provider<BookDetailsViewModel>
    private val binding by viewBinding(TabSettingsBinding::bind)
    private val viewModel by daggerParentActivityViewModel { viewModelProvider }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() = binding.run {
        deleteBook.setOnClickListener {
            viewModel.deleteBook(onDelete = {
                findNavController().navigate(R.id.action_bookDetailsFragment_to_libraryFragment)
                context?.showShortToast(R.string.book_successfully_deleted)
            })
        }
        addSession.setOnClickListener {
            //todo
        }
        editBook.setOnClickListener {
            val bundle = Bundle().apply {
                putLong(BookCreationFragment.EDITED_BOOK_ID, viewModel.getCurrentBookId())
            }
            findNavController().navigate(R.id.bookCreationFragment, bundle)
        }
    }
}