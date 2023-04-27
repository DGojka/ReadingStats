package com.example.bookstats.features.library.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstats.databinding.BookItemBinding

class BookAdapter(private val onBookClick: (id: Int) -> Unit) :
    ListAdapter<BookItem, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    inner class BookViewHolder(private val binding: BookItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: BookItem) {
            with(binding) {
                bookImage.setImageResource(book.bookImage)
                bookTitle.text = book.bookTitle
                root.setOnClickListener { onBookClick(book.bookId) }
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

    fun setData(data: List<BookItem>?) {
        submitList(data)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }

}
