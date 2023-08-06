package com.example.bookstats.features.bookdetails.tabs

import androidx.recyclerview.widget.RecyclerView
import com.example.bookstats.databinding.TabSettingsBinding

class SettingsTabViewHolder(
    private val binding: TabSettingsBinding,
    private val listener: TabListener.SettingsTabListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind() {
        with(binding) {
            deleteBook.setOnClickListener {
                listener.onDeleteBook()
            }
            addSession.setOnClickListener {
                listener.onAddSession()
            }
            editBook.setOnClickListener {
                listener.onEditBook()
            }
        }
    }
}