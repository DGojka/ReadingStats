package com.example.bookstats.features.library.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstats.databinding.BookItemBinding
import com.example.bookstats.repository.BookWithSessions

class BookAdapter(private val onBookClick: (id: Int) -> Unit) :
    ListAdapter<BookWithSessions, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    inner class BookViewHolder(private val binding: BookItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: BookWithSessions) {
            with(binding) {
                bookImage.setImageBitmap(book.image)
                root.setOnClickListener { onBookClick(book.id.toInt()) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(
            BookItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun setData(data: List<BookWithSessions>?) {
        submitList(data)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }

}
