package com.example.bookstats.features.realtimesessions.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookstats.R
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.FragmentSessionSummaryBinding
import com.example.bookstats.extensions.daggerParentActivityViewModel
import com.example.bookstats.extensions.hideBottomNavigationView
import com.example.bookstats.extensions.showBottomNavigationView
import com.example.bookstats.extensions.viewBinding
import com.example.bookstats.features.bookdetails.tabs.sessions.helpers.SessionDetails
import com.example.bookstats.features.realtimesessions.viewmodel.RealTimeSessionsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class SummaryFragment : Fragment(R.layout.fragment_session_summary) {

    @Inject
    lateinit var viewModelProvider: Provider<RealTimeSessionsViewModel>

    private val binding by viewBinding(FragmentSessionSummaryBinding::bind)
    private val viewModel by daggerParentActivityViewModel { viewModelProvider }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        hideBottomNavigationView()
        binding.finishButton.setOnClickListener {
            findNavController().popBackStack(R.id.bookDetailsFragment, false)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.sessionDetails?.let {
                    bindViews(it)
                }
            }
        }
    }

    private fun bindViews(sessionDetails: SessionDetails) = binding.run {
        with(sessionDetails) {
            avgMinPageValue.text = avgMinPerPage
            pagesReadValue.text = pagesRead
            avgPagesHourValue.text = avgPagesPerHour
            readTimeValue.text = readTime
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigationView()
    }
}