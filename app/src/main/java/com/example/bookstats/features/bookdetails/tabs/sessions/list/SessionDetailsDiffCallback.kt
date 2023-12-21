package com.example.bookstats.features.bookdetails.tabs.sessions.list

import androidx.recyclerview.widget.DiffUtil
import com.example.bookstats.features.bookdetails.tabs.sessions.helpers.SessionDetails

class SessionDetailsDiffCallback : DiffUtil.ItemCallback<SessionDetails>() {
    override fun areItemsTheSame(oldItem: SessionDetails, newItem: SessionDetails): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: SessionDetails, newItem: SessionDetails): Boolean {
        return oldItem == newItem
    }
}
