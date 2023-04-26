package com.example.bookstats.features.library.list

import androidx.recyclerview.widget.DiffUtil

class BookDiffCallback : DiffUtil.ItemCallback<BookItem>() {
    override fun areItemsTheSame(oldItem: BookItem, newItem: BookItem): Boolean {
        // Return true if the items have the same ID
        return oldItem.bookId == newItem.bookId
    }

    override fun areContentsTheSame(oldItem: BookItem, newItem: BookItem): Boolean {
        // Return true if the items have the same data
        return oldItem == newItem
    }
}