package com.example.bookstats.features.library.tabs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.bookstats.databinding.TabGeneralBinding
import com.example.bookstats.databinding.TabSessionsBinding
import com.example.bookstats.databinding.TabSettingsBinding
import com.example.bookstats.features.library.tabs.general.helpers.GeneralBookInfo
import com.example.bookstats.features.library.tabs.general.viewholders.GeneralTabViewHolder
import com.example.bookstats.features.library.tabs.sessions.SessionListItem
import com.example.bookstats.features.library.tabs.sessions.SessionsTabViewHolder
import com.example.bookstats.features.library.viewmodel.LibraryViewModel
import com.example.bookstats.repository.BookWithSessions

class ViewPagerAdapter(
    private val viewModel: LibraryViewModel,
    private val onStartSessionClick: () -> Unit
) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    private var generalBookInfo = GeneralBookInfo()
    private var sessionsList: List<SessionListItem> = mutableListOf()

    inner class ViewHolder(viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root)

    private val viewTypeList =
        listOf(ViewType.TabSettings, ViewType.TabGeneral, ViewType.TabSessions)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewTypeList[viewType]) {
            ViewType.TabSettings -> TabSettingsBinding.inflate(inflater, parent, false)
            ViewType.TabGeneral -> TabGeneralBinding.inflate(inflater, parent, false)
            ViewType.TabSessions -> TabSessionsBinding.inflate(inflater, parent, false)
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                //TODO: Implement
            }
            1 -> {
                val binding = TabGeneralBinding.bind(holder.itemView)
                val generalTabViewHolder = GeneralTabViewHolder(binding, onStartSessionClick)
                generalTabViewHolder.bind(generalBookInfo)
            }
            2 -> {
                val binding = TabSessionsBinding.bind(holder.itemView)
                val sessionTabViewHolder = SessionsTabViewHolder(binding)
                sessionTabViewHolder.bind(sessionsList)
            }
        }
    }

    fun updateBookInfo(book: BookWithSessions) {
        with(book) {
            generalBookInfo = GeneralBookInfo(
                bookName = name,
                bookAuthor = author,
                bookImage = "", //TODO: Implement
                avgReadingTime = viewModel.getAvgReadingTime(sessions),
                avgPagesPerHour = viewModel.getAvgPagesPerHour(sessions),
                avgMinutesPerPage = viewModel.getAvgMinPerPage(sessions),
                totalReadTime = viewModel.getTotalReadTime(sessions)
            )
            notifyDataSetChanged()
        }
    }

    fun updateSessionsList(sessions: List<SessionListItem>) {
        sessionsList = sessions
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = viewTypeList.size


    override fun getItemViewType(position: Int): Int = position

}


