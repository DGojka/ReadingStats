package com.example.bookstats.features.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.bookstats.R
import com.example.bookstats.activity.MainActivity
import com.example.bookstats.app.ReadingStatsApp
import com.example.bookstats.databinding.FragmentBookDetailsBinding
import com.example.bookstats.features.library.managers.SessionDialogManager
import com.example.bookstats.features.library.tabs.ViewPagerAdapter
import com.example.bookstats.features.library.viewmodel.LibraryViewModel
import com.example.bookstats.features.library.viewmodel.LibraryViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import javax.inject.Inject


class BookDetailsFragment : Fragment() {
    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: LibraryViewModelFactory
    private val viewModel by viewModels<LibraryViewModel>({ activity as MainActivity }) { viewModelFactory }

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var bookIdBundle: Bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as ReadingStatsApp).appComponent.inject(this)
        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)
        viewPagerAdapter = ViewPagerAdapter(
            viewModel,
            onStartSessionClick = {
                findNavController().navigate(
                    R.id.action_bookDetailsFragment_to_sessionFragment,
                    bookIdBundle
                )
            })
        initViewPager()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSessionDialogManager()
        initPercentage()
        observeState()
        initDeleteButton()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                with(state) {
                    bookClicked?.let { book ->
                        bookIdBundle.putString("id", book.id.toString())
                        viewPagerAdapter.updateBookInfo(book)
                        viewPagerAdapter.updateSessionsList(
                            viewModel.mapSessionsToSessionListItem(
                                book.sessions
                            )
                        )
                    }
                }
            }
        }
    }

    private fun initPercentage() {
        binding.bookProgressbar.progress = viewModel.getBookPercentage()
        binding.percentageTextView.text =
            getString(R.string.book_percentage, viewModel.getBookPercentage())
    }

    private fun initViewPager() {
        val viewPager = binding.viewPager2
        val tabLayout = binding.tabLayout
        tabLayout.addTab(tabLayout.newTab().setText(SETTINGS))
        tabLayout.addTab(tabLayout.newTab().setText(GENERAL))
        tabLayout.addTab(tabLayout.newTab().setText(SESSIONS))
        tabLayout.setTabTextColors(
            ContextCompat.getColor(requireContext(), R.color.whisper),
            ContextCompat.getColor(requireContext(), R.color.dark_violet)
        )
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = SETTINGS
                1 -> tab.text = GENERAL
                2 -> tab.text = SESSIONS
            }
        }.attach()
        viewPager.currentItem = 1
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    private fun initSessionDialogManager() {
        binding.addSession.setOnClickListener {
            SessionDialogManager(layoutInflater, viewModel).showAddSessionDialog()
        }
    }

    private fun initDeleteButton() {
        binding.delete.setOnClickListener {
            viewModel.deleteBook(navigate = {
                findNavController().navigate(R.id.action_bookDetailsFragment_to_libraryFragment)
                Toast.makeText(requireContext(), "Book successfully deleted!", Toast.LENGTH_SHORT)
                    .show()
            })
        }
    }

    companion object {
        private const val SETTINGS = "Settings"
        private const val GENERAL = "General"
        private const val SESSIONS = "Sessions"
    }
}
