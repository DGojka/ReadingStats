package com.example.bookstats.features.realtimesessions.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bookstats.R
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.FragmentRealTimeSessionBinding
import com.example.bookstats.databinding.PagesReadDialogBinding
import com.example.bookstats.extensions.*
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerBroadcastListener
import com.example.bookstats.features.realtimesessions.viewmodel.Error
import com.example.bookstats.features.realtimesessions.viewmodel.RealTimeSessionsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class RealTimeSessionFragment : Fragment(R.layout.fragment_real_time_session),
    TimerBroadcastListener {

    @Inject
    lateinit var viewModelProvider: Provider<RealTimeSessionsViewModel>

    private val binding by viewBinding(FragmentRealTimeSessionBinding::bind)
    private val viewModel by daggerParentActivityViewModel { viewModelProvider }

    private lateinit var endSessionDialog: AlertDialog

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            exitWithoutSaving()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigationView()
        setupBackButtonCallback()
        viewModel.startSession()
        setupButtons()
        setupEndSessionDialog()
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.timer.text = state.currentTime
                state.error?.let {
                    handleError(it)
                }
            }
        }
    }

    private fun handleError(error: Error) {
        with(error) {
            if (reason is Error.Reason.NewPageIsLowerThanOld) {
                requireContext().showLongToast(
                    getString(
                        R.string.page_must_be_higher,
                        reason.currentPage.toString()
                    )
                )
            } else if (reason is Error.Reason.NewPageIsGreaterThanTotalBookPages) {
                requireContext().showLongToast(R.string.page_is_too_high)
            }
        }
    }

    private fun setupButtons() = binding.run {
        backButton.setOnClickListener {
            exitWithoutSaving()
        }
        stop.setOnClickListener {
            viewModel.pauseTimer()
            endSessionDialog.show()
        }
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

    private fun setupEndSessionDialog() {
        val dialogBinding = PagesReadDialogBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(layoutInflater.context)
            .setView(dialogBinding.root)
            .setTitle(R.string.current_page_in_this_session)

        with(dialogBinding) {
            dialogBinding.save.setOnClickListener {
                if (pagesReadTextView.text.toString().isNotEmpty()) {
                    viewModel.stopSession()
                    viewModel.saveSession(
                        newCurrentPage = pagesReadTextView.text.toString().toInt()
                    ) {
                        endSessionDialog.dismiss()
                        findNavController().navigate(R.id.realtimesession_to_summary)
                    }
                }
            }
        }
        endSessionDialog = dialogBuilder.create()
    }

    private fun exitWithoutSaving() {
        viewModel.endSessionWithoutSaving()
        findNavController().popBackStack()
    }

    private fun setupBackButtonCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
    }

    override fun onPause() {
        super.onPause()
        if (!viewModel.isSessionEnded()) {
            viewModel.pauseTimer()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumeTimerState()
    }

    override fun onTimerBroadcastReceiver(currentMs: Float) {
        viewModel.setCurrentMs(currentMs)
    }

}