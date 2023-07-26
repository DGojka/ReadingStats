package com.example.bookstats.features.realtimesessions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookstats.R
import com.example.bookstats.activity.MainActivity
import com.example.bookstats.activity.MainActivity.Companion.hideBottomNavigationView
import com.example.bookstats.activity.MainActivity.Companion.showBottomNavigationView
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.FragmentSessionSummaryBinding
import com.example.bookstats.features.realtimesessions.viewmodel.RealTimeSessionsViewModel
import com.example.bookstats.features.realtimesessions.viewmodel.RealTimeSessionsViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class SummaryFragment : Fragment() {

    private var _binding: FragmentSessionSummaryBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: RealTimeSessionsViewModelFactory
    private val viewModel by viewModels<RealTimeSessionsViewModel>({ activity as MainActivity }) { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appComponent.inject(this)
        hideBottomNavigationView()
        _binding = FragmentSessionSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        binding.finishButton.setOnClickListener {
            findNavController().popBackStack(R.id.bookDetailsFragment,false)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                with(viewModel.mapSessionsToSessionListItem(session = state.session!!)){
                    binding.avgMinPageValue.text = avgMinPerPage
                    binding.pagesReadValue.text = pagesRead
                    binding.avgPagesHourValue.text = avgPagesPerHour
                    binding.readTimeValue.text = readTime
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showBottomNavigationView()
    }
}