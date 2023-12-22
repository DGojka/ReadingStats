package com.example.bookstats.features.bookdetails

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bookstats.R
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.FragmentBookDetailsBinding
import com.example.bookstats.extensions.daggerParentActivityViewModel
import com.example.bookstats.extensions.hideBottomNavigationView
import com.example.bookstats.extensions.showBottomNavigationView
import com.example.bookstats.extensions.viewBinding
import com.example.bookstats.features.bookdetails.tabs.BookDetailsTabsAdapter
import com.example.bookstats.features.bookdetails.viewmodel.BookDetailsViewModel
import com.example.bookstats.features.bookdetails.viewmodel.uistate.BookDetailsUiState
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


class BookDetailsFragment : Fragment(R.layout.fragment_book_details) {

    @Inject
    lateinit var viewModelProvider: Provider<BookDetailsViewModel>

    private val binding by viewBinding(FragmentBookDetailsBinding::bind)
    private val viewModel by daggerParentActivityViewModel { viewModelProvider }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        viewModel.init()
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateBookPercentage(state)
            }
        }
    }

    private fun updateBookPercentage(state: BookDetailsUiState) {
        binding.bookProgressbar.progress = state.bookPercentage
        binding.percentageTextView.text =
            getString(R.string.book_percentage, state.bookPercentage)
    }

    private fun setupViewPager() {
        val viewPager = binding.viewPager2
        val tabLayout = binding.tabLayout
        val adapter = BookDetailsTabsAdapter(requireActivity())
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = SETTINGS
                1 -> tab.text = GENERAL
                2 -> tab.text = SESSIONS
            }
        }.attach()
        viewPager.setCurrentItem(1, false)
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigationView()
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigationView()
    }

    companion object {
        private const val SETTINGS = "Settings"
        private const val GENERAL = "General"
        private const val SESSIONS = "Sessions"
    }
}
