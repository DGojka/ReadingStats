package com.example.bookstats.features.realtimesessions.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookstats.R
import com.example.bookstats.app.ReadingStatsApp
import com.example.bookstats.databinding.FragmentRealTimeSessionBinding
import com.example.bookstats.databinding.PagesReadDialogBinding
import com.example.bookstats.features.realtimesessions.viewmodel.RealTimeSessionsViewModel
import com.example.bookstats.features.realtimesessions.viewmodel.RealTimeSessionsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import javax.inject.Inject

class RealTimeSessionFragment : Fragment() {
    private var _binding: FragmentRealTimeSessionBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: RealTimeSessionsViewModelFactory
    private lateinit var viewModel: RealTimeSessionsViewModel
    private lateinit var pagesReadDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as ReadingStatsApp).appComponent.inject(this)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
        _binding = FragmentRealTimeSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[RealTimeSessionsViewModel::class.java]
        viewModel.startSession()
        initStopButton()
        initPauseResumeButton()
        initShowPagesDialog()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                setTime(currentMs = state.currentMs)
            }
        }
    }

    private fun setTime(currentMs: Float) {
        with(binding) {
            timer.text = getTimerText(currentMs)
        }
    }

    private fun initStopButton() {
        binding.stop.setOnClickListener {
            viewModel.stopSession()
            pagesReadDialog.show()
        }
    }

    private fun initPauseResumeButton() {
        binding.pauseOrResumeButton.setOnClickListener {
            with(viewModel) {
                binding.pauseOrResumeButton.apply {
                    if (isTimerPaused()) {
                        setImageResource(R.drawable.ic_pause_session)
                        resumeTimer()
                    } else {
                        setImageResource(R.drawable.ic_resume_session)
                        pauseTimer()
                    }
                }
            }
        }
    }

    private fun getTimerText(currentMs: Float): CharSequence {
        val totalSeconds = (currentMs / 1000).toInt()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun initShowPagesDialog() {
        val dialogBinding = PagesReadDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(layoutInflater.context)
            .setView(dialogBinding.root)
            .setTitle(R.string.pages_read_in_this_session)
        with(dialogBinding) {
            dialogBinding.save.setOnClickListener {
                viewModel.saveSession(
                    bookId = requireArguments().getString("id")!!.toLong(),
                    pagesRead = pagesReadTextView.text.toString()
                ) {
                    findNavController().popBackStack()
                }
                pagesReadDialog.dismiss()
            }
        }
        pagesReadDialog = dialogBuilder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
    }
}