package com.example.bookstats.features.library.tabs.sessions

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
