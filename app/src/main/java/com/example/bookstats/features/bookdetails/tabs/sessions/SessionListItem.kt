package com.example.bookstats.features.bookdetails.tabs.sessions

data class SessionListItem(
    var date: String,
    var pagesRead: String,
    var readTime: String,
    var avgMinPerPage: String,
    var avgPagesPerHour: String
)