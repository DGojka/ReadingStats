package com.example.bookstats.features.library.tabs.sessions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstats.databinding.SessionListItemBinding
import com.example.bookstats.repository.Session

class SessionAdapter(private val sessionList: List<Session>) :
    RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    inner class SessionViewHolder(private val binding: SessionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(session: Session) {
            with(binding) {
                session.apply {
                    pagesReadValue.text = pagesRead.toString()
                    readTimeValue.text = sessionTimeSeconds.toString()
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
