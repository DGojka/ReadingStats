package com.example.bookstats.features.bookdetails.tabs.sessions

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bookstats.R
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.TabSessionsBinding
import com.example.bookstats.extensions.daggerParentActivityViewModel
import com.example.bookstats.extensions.viewBinding
import com.example.bookstats.features.bookdetails.tabs.sessions.list.SessionAdapter
import com.example.bookstats.features.bookdetails.viewmodel.BookDetailsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class BookSessionsFragment : Fragment(R.layout.tab_sessions) {

    @Inject
    lateinit var viewModelProvider: Provider<BookDetailsViewModel>
    private val binding by viewBinding(TabSessionsBinding::bind)
    private val viewModel by daggerParentActivityViewModel { viewModelProvider }

    private lateinit var sessionAdapter: SessionAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSessionList()
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.sessionDetails?.let {
                    sessionAdapter.submitList(it)
                }
            }
        }
    }

    private fun setupSessionList() {
        sessionAdapter = SessionAdapter()
        binding.recyclerView.adapter = sessionAdapter
    }
}
