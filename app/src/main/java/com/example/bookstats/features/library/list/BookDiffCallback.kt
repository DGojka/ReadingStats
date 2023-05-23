package com.example.bookstats.features.library.list

import androidx.recyclerview.widget.DiffUtil
import com.example.bookstats.repository.BookWithSessions

class BookDiffCallback : DiffUtil.ItemCallback<BookWithSessions>() {
    override fun areItemsTheSame(oldItem: BookWithSessions, newItem: BookWithSessions): Boolean {
        // Return true if the items have the same ID
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BookWithSessions, newItem: BookWithSessions): Boolean {
        // Return true if the items have the same data
        return oldItem == newItem
    }
}