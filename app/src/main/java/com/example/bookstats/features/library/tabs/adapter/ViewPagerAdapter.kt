package com.example.bookstats.features.library.tabs.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.bookstats.R
import com.example.bookstats.databinding.TabGeneralBinding
import com.example.bookstats.databinding.TabSessionsBinding
import com.example.bookstats.databinding.TabSettingsBinding
import com.example.bookstats.features.library.tabs.adapter.helpers.GeneralBookInfo
import com.example.bookstats.features.library.tabs.adapter.viewholders.GeneralTabViewHolder
import com.example.bookstats.features.library.viewmodel.LibraryViewModel
import com.example.bookstats.repository.BookWithSessions

class ViewPagerAdapter(
    private val viewModel: LibraryViewModel
) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    private var generalBookInfo = GeneralBookInfo()
    inner class ViewHolder(viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root)

    private val layoutIds = arrayOf(
        R.layout.tab_settings,
        R.layout.tab_general,
        R.layout.tab_sessions
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            0 -> TabSettingsBinding.inflate(inflater, parent, false)
            1 -> TabGeneralBinding.inflate(inflater, parent, false)
            2 -> TabSessionsBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Invalid viewType")
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                //TODO later
            }
            1 -> {
                val binding = TabGeneralBinding.bind(holder.itemView)
                val generalTabViewHolder = GeneralTabViewHolder(binding)
                generalTabViewHolder.bind(generalBookInfo)
            }
            2 -> {
            }
        }
    }

    fun updateBookInfo(book: BookWithSessions) {
        with(book) {
            generalBookInfo = GeneralBookInfo(
                bookName = name,
                bookAuthor = author,
                bookImage = "", //TODO later
                avgReadingTime = viewModel.getAvgReadingTime(sessions),
                avgPagesPerHour = viewModel.getAvgPagesPerHour(sessions),
                avgMinutesPerPage = viewModel.getAvgMinPerPage(sessions),
                totalReadTime = viewModel.getTotalReadTime(sessions))
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return layoutIds.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}


