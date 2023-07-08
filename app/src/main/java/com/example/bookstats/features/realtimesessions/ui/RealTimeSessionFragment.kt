package com.example.bookstats.features.realtimesessions.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookstats.R
import com.example.bookstats.app.ReadingStatsApp
import com.example.bookstats.databinding.FragmentRealTimeSessionBinding
import com.example.bookstats.databinding.PagesReadDialogBinding
import com.example.bookstats.features.realtimesessions.TimerBroadcastListener
import com.example.bookstats.features.realtimesessions.helpers.CurrentBookDb
import com.example.bookstats.features.realtimesessions.viewmodel.Error
import com.example.bookstats.features.realtimesessions.viewmodel.RealTimeSessionsViewModel
import com.example.bookstats.features.realtimesessions.viewmodel.RealTimeSessionsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import javax.inject.Inject

class RealTimeSessionFragment : Fragment(), TimerBroadcastListener {
    private var _binding: FragmentRealTimeSessionBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: RealTimeSessionsViewModelFactory
    private lateinit var viewModel: RealTimeSessionsViewModel

    @Inject
    lateinit var currentBookDb: CurrentBookDb

    private lateinit var endSessionDialog: AlertDialog

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.pauseTimer()
            viewModel.endSessionWithoutSaving()
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as ReadingStatsApp).appComponent.inject(this)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
        _binding = FragmentRealTimeSessionBinding.inflate(inflater, container, false)
       requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[RealTimeSessionsViewModel::class.java]
        viewModel.startSession()
        initStopButton()
        initPauseResumeButton()
        initShowPagesDialog()
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                setTime(currentMs = state.currentMs)
                with(state.error) {
                    if (this != null) {
                        if (reason is Error.Reason.NewPageIsLowerThanOld) {
                            Toast.makeText(
                                requireContext(),
                                getString(
                                    R.string.page_must_be_higher,
                                    reason.currentPage.toString()
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (reason is Error.Reason.NewPageIsGreaterThanTotalBookPages) {
                            Toast.makeText(
                                requireContext(),
                                R.string.page_is_too_high,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
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
            viewModel.pauseTimer()
            endSessionDialog.show()
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
            .setTitle(R.string.current_page_in_this_session)

        with(dialogBinding) {
            dialogBinding.save.setOnClickListener {
                if (pagesReadTextView.text.toString().isNotEmpty()) {
                    viewModel.stopSession()
                    viewModel.saveSession(
                        bookId = currentBookDb.getCurrentBookId(),
                        newCurrentPage = pagesReadTextView.text.toString().toInt()
                    ) {
                        endSessionDialog.dismiss()
                        findNavController().popBackStack()
                    }
                }
            }
        }
        endSessionDialog = dialogBuilder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
       onBackPressedCallback.remove()
    }

    override fun onTimerBroadcastReceiver(currentMs: Float) {
        viewModel.setCurrentMs(currentMs)
    }

}