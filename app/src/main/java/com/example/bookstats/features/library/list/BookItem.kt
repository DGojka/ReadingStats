package com.example.bookstats.features.library.list

import android.graphics.Bitmap

//TEMPORARY CLASS, will be removed after adding ability to upload images to the book
data class BookItem(val bookId: Int, val bookImage: Int, val bookTitle: String, val image: Bitmap)
