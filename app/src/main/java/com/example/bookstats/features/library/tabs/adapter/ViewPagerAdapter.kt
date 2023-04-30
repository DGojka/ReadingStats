package com.example.bookstats.features.library.tabs.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bookstats.features.library.tabs.GeneralTab
import com.example.bookstats.features.library.tabs.SessionsTab
import com.example.bookstats.features.library.tabs.SettingsTab

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SettingsTab()
            1 -> GeneralTab()
            2 -> SessionsTab()
            else -> throw IllegalStateException("Invalid tab position")
        }
    }
}
