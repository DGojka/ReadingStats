package com.example.bookstats.features.library.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstats.databinding.BookItemBinding
import com.example.bookstats.extensions.viewBinding
import com.example.bookstats.repository.BookWithSessions

class BookAdapter(private val onBookClick: (id: Int) -> Unit) :
    ListAdapter<BookWithSessions, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    inner class BookViewHolder(private val binding: BookItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: BookWithSessions) = binding.run {
            bookImage.setImageBitmap(book.image)
            root.setOnClickListener { onBookClick(book.id.toInt()) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder =
        BookViewHolder(parent.viewBinding(BookItemBinding::inflate))

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) =
        holder.bind(getItem(position))
}
