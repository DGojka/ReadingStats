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
import com.example.bookstats.features.library.tabs.adapter.ViewPagerAdapter
import com.example.bookstats.features.library.viewmodel.LibraryViewModel
import com.example.bookstats.features.library.viewmodel.LibraryViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import javax.inject.Inject


class BookDetailsFragment() : Fragment() {
    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    @Inject
    lateinit var viewModelFactory: LibraryViewModelFactory
    private val viewModel by viewModels<LibraryViewModel>({ activity as MainActivity }) { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as ReadingStatsApp).appComponent.inject(this)
        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)
        val sessionDialogManager = SessionDialogManager(layoutInflater, viewModel)
        binding.addSession.setOnClickListener {
            sessionDialogManager.showAddSessionDialog()
        }
        viewPagerAdapter = ViewPagerAdapter(viewModel)
        initPages()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPercentage()
        observeState()
        binding.delete.setOnClickListener {
            viewModel.deleteBook(navigate = {
                findNavController().navigate(R.id.action_bookDetailsFragment_to_libraryFragment)
                Toast.makeText(requireContext(), "Book successfully deleted!", Toast.LENGTH_SHORT)
                    .show()
            })
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                with(state) {
                        viewPagerAdapter.updateBookInfo(bookClicked!!)
                }
            }
        }
    }

    private fun initPercentage() {
        binding.bookProgressbar.progress = viewModel.getBookPercentage()
        binding.percentageTextView.text = viewModel.getBookPercentage().toString() + "%"
    }

    private fun initPages() {
        val viewPager = binding.viewPager2
        val tabLayout = binding.tabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Settings"))
        tabLayout.addTab(tabLayout.newTab().setText("General"))
        tabLayout.addTab(tabLayout.newTab().setText("Sessions"))
        tabLayout.setTabTextColors(
            ContextCompat.getColor(requireContext(), R.color.whisper),
            ContextCompat.getColor(requireContext(), R.color.dark_violet)
        )
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Settings"
                1 -> tab.text = "General"
                2 -> tab.text = "Sessions"
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

}
