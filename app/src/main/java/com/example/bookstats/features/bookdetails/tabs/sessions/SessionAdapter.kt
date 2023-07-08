package com.example.bookstats.features.bookdetails.tabs.sessions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstats.databinding.SessionListItemBinding

class SessionAdapter(private var sessionList: List<SessionListItem>) :
    RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    inner class SessionViewHolder(private val binding: SessionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(session: SessionListItem) {
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
        SessionViewHolder(
            SessionListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessionList[position]
        holder.bind(session)
    }

    override fun getItemCount() = sessionList.size
}