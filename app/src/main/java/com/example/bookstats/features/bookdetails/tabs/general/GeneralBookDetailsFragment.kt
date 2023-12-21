package com.example.bookstats.features.bookdetails.tabs.general

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.bookstats.R
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.TabGeneralBinding
import com.example.bookstats.extensions.daggerParentActivityViewModel
import com.example.bookstats.extensions.viewBinding
import com.example.bookstats.features.bookdetails.tabs.general.helpers.GeneralBookInfo
import com.example.bookstats.features.bookdetails.viewmodel.BookDetailsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class GeneralBookDetailsFragment : Fragment(R.layout.tab_general) {

    @Inject
    lateinit var viewModelProvider: Provider<BookDetailsViewModel>
    private val binding by viewBinding(TabGeneralBinding::bind)
    private val viewModel by daggerParentActivityViewModel { viewModelProvider }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                with(state) {
                    bindViews(generalBookInfo = bookInfo!!)
                }
            }
        }
    }

    private fun bindViews(generalBookInfo: GeneralBookInfo) {
        with(binding) {
            generalBookInfo.apply {
                generalTabBookName.text = bookName
                generalTabBookAuthor.text = bookAuthor
                generalAvgSessionTimeValue.text = avgReadingTime
                generalAvgMinPageValue.text = avgMinutesPerPage
                generalAvgPagesHourValue.text = avgPagesPerHour
                generalTotalReadTimeValue.text = totalReadTime
                bookCurrentPage.text = requireContext().getString(
                    R.string.current_page_text,
                    currentPage,
                    maxPage
                )
                binding.generalTabBookImage.load(image)
            }
            continueReading.setOnClickListener {
                findNavController().navigate(R.id.action_bookDetailsFragment_to_sessionFragment)
            }
        }
    }
}