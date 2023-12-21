package com.example.bookstats.features.bookdetails.tabs.sessions.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstats.databinding.SessionListItemBinding
import com.example.bookstats.extensions.viewBinding
import com.example.bookstats.features.bookdetails.tabs.sessions.helpers.SessionDetails

class SessionAdapter : ListAdapter<SessionDetails, SessionAdapter.SessionViewHolder>(
    SessionDetailsDiffCallback()
) {

    inner class SessionViewHolder(val binding: SessionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(session: SessionDetails) {
            with(binding) {
                session.apply {
                    pagesReadValue.text = pagesRead
                    readTimeValue.text = readTime
                    avgMinPageValue.text = avgMinPerPage
                    avgPagesHourValue.text = avgPagesPerHour
                    dateTextView.text = date
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder =
        SessionViewHolder(parent.viewBinding(SessionListItemBinding::inflate))

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) =
        holder.bind(getItem(position))

}
