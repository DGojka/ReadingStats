package com.example.bookstats.features.bookdetails.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bookstats.features.bookdetails.tabs.general.GeneralBookDetailsFragment
import com.example.bookstats.features.bookdetails.tabs.sessions.BookSessionsFragment
import com.example.bookstats.features.bookdetails.tabs.settings.BookSettingsFragment

class BookDetailsTabsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            VIEW_SETTINGS_TAB -> BookSettingsFragment()
            VIEW_GENERAL_TAB -> GeneralBookDetailsFragment()
            VIEW_SESSIONS_TAB -> BookSessionsFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getItemCount(): Int = PAGE_COUNT

    companion object {
        private const val VIEW_SETTINGS_TAB = 0
        private const val VIEW_GENERAL_TAB = 1
        private const val VIEW_SESSIONS_TAB = 2
        private const val PAGE_COUNT = 3
    }
}
