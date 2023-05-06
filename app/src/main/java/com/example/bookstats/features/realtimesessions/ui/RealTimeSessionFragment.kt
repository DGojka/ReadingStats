package com.example.bookstats.features.realtimesessions.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.bookstats.app.ReadingStatsApp
import com.example.bookstats.databinding.FragmentRealTimeSessionBinding
import com.example.bookstats.features.realtimesessions.viewmodel.RealTimeSessionsViewModel
import com.example.bookstats.features.realtimesessions.viewmodel.RealTimeSessionsViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class RealTimeSessionFragment : Fragment() {
    private var _binding: FragmentRealTimeSessionBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: RealTimeSessionsViewModelFactory
    private lateinit var viewModel: RealTimeSessionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as ReadingStatsApp).appComponent.inject(this)
        _binding = FragmentRealTimeSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[RealTimeSessionsViewModel::class.java]
        viewModel.startSession(bookId = requireArguments().getString("id")!!.toLong())

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                Log.e("asd", state.currentMs.toString())
            }
        }
    }
}