package com.example.bookstats.features.bookdetails.tabs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.bookstats.databinding.TabGeneralBinding
import com.example.bookstats.databinding.TabSessionsBinding
import com.example.bookstats.databinding.TabSettingsBinding
import com.example.bookstats.features.bookdetails.tabs.general.helpers.GeneralBookInfo
import com.example.bookstats.features.bookdetails.tabs.general.viewholders.GeneralTabViewHolder
import com.example.bookstats.features.bookdetails.tabs.sessions.SessionDetails
import com.example.bookstats.features.bookdetails.tabs.sessions.SessionsTabViewHolder
import com.example.bookstats.features.bookdetails.viewmodel.BookDetailsViewModel
import com.example.bookstats.repository.BookWithSessions

class ViewPagerAdapter(
    private val viewModel: BookDetailsViewModel,
    private val generalTabListener: TabListener.GeneralTabListener,
    private val settingsTabListener: TabListener.SettingsTabListener
) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    private lateinit var generalBookInfo: GeneralBookInfo
    private var sessionsList: List<SessionDetails> = mutableListOf()

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
                val binding = TabSettingsBinding.bind(holder.itemView)
                val settingsTabViewHolder = SettingsTabViewHolder(binding, settingsTabListener)
                settingsTabViewHolder.bind()
            }
            1 -> {
                val binding = TabGeneralBinding.bind(holder.itemView)
                val generalTabViewHolder =
                    GeneralTabViewHolder(binding) { generalTabListener.onSessionStart() }
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
                image = image,
                avgReadingTime = viewModel.getAvgReadingTime(sessions),
                avgPagesPerHour = viewModel.getAvgPagesPerHour(sessions),
                avgMinutesPerPage = viewModel.getAvgMinPerPage(sessions),
                totalReadTime = viewModel.getTotalReadTime(sessions),
                currentPage = currentPage.toString(),
                maxPage = totalPages.toString()
            )
            notifyDataSetChanged()
        }
    }

    fun updateSessionsList(sessions: List<SessionDetails>) {
        sessionsList = sessions
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = viewTypeList.size


    override fun getItemViewType(position: Int): Int = position

}


