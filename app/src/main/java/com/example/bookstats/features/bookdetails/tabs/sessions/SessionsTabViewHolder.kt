package com.example.bookstats.features.bookdetails.tabs.sessions

import androidx.recyclerview.widget.RecyclerView
import com.example.bookstats.databinding.TabSessionsBinding

class SessionsTabViewHolder(private val binding: TabSessionsBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(sessionList: List<SessionListItem>) {
        binding.recyclerView.adapter = SessionAdapter(sessionList = sessionList.asReversed())
    }
}