package com.example.bookstats.features.bookdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.bookstats.R
import com.example.bookstats.activity.MainActivity.Companion.hideBottomNavigationView
import com.example.bookstats.activity.MainActivity.Companion.showBottomNavigationView
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.FragmentBookDetailsBinding
import com.example.bookstats.features.bookdetails.managers.SessionDialogManager
import com.example.bookstats.features.bookdetails.tabs.TabListener
import com.example.bookstats.features.bookdetails.tabs.ViewPagerAdapter
import com.example.bookstats.features.bookdetails.viewmodel.BookDetailsViewModel
import com.example.bookstats.features.bookdetails.viewmodel.BookDetailsViewModelFactory
import com.example.bookstats.features.creation.BookCreationFragment.Companion.EDITED_BOOK_ID
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import javax.inject.Inject


class BookDetailsFragment : Fragment() {
    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: BookDetailsViewModelFactory
    private lateinit var viewModel: BookDetailsViewModel

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory)[BookDetailsViewModel::class.java]
        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)
        initViewPagerAdapter()
        viewModel.refreshBookClicked()
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
                    book?.let { book ->
                        viewPagerAdapter.updateBookInfo(book)
                        viewPagerAdapter.updateSessionsList(
                            viewModel.mapSessionsToSessionDetails(
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

    private fun initViewPagerAdapter() {
        viewPagerAdapter = ViewPagerAdapter(
            viewModel,
            object : TabListener.GeneralTabListener {
                override fun onSessionStart() {
                    findNavController().navigate(R.id.action_bookDetailsFragment_to_sessionFragment)
                }
            },
            object : TabListener.SettingsTabListener {
                override fun onDeleteBook() {
                    viewModel.deleteBook { findNavController().navigate(R.id.libraryFragment) }
                }

                override fun onAddSession() {
                    TODO("not implemented")
                }

                override fun onEditBook() {
                    var editedBookId: Long? = null
                    viewModel.editBook { editedBookId = it }
                    if (editedBookId != null) {
                        val bundle = Bundle().apply {
                            putLong(EDITED_BOOK_ID, editedBookId!!)
                        }
                        findNavController().navigate(R.id.bookCreationFragment, bundle)
                    }
                }

            }
        )
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
            viewModel.deleteBook(onDelete = {
                findNavController().navigate(R.id.action_bookDetailsFragment_to_libraryFragment)
                Toast.makeText(
                    requireContext(),
                    R.string.book_successfully_deleted,
                    Toast.LENGTH_SHORT
                )
                    .show()
            })
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigationView() //temp?
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
