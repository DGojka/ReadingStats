package com.example.bookstats.features.bookdetails.tabs.general.helpers

import android.graphics.Bitmap

data class GeneralBookInfo(
    val bookName: String = "",
    val bookAuthor: String = "",
    val image: Bitmap? = null,
    val avgReadingTime: String = "",
    val avgPagesPerHour: String = "",
    val avgMinutesPerPage: String = "",
    val totalReadTime: String = ""
)
