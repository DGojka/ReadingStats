package com.example.bookstats.features.bookdetails.tabs.general.helpers

import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.repository.BookWithSessions

fun BookWithSessions.mapToGeneralBookInfo(sessionCalculator: SessionCalculator): GeneralBookInfo {
    sessionCalculator.apply {
        return GeneralBookInfo(
            bookName = name,
            bookAuthor = author,
            image = image,
            avgReadingTime = getAvgReadingTime(sessions),
            avgPagesPerHour = getAvgPagesPerHour(sessions),
            avgMinutesPerPage = getAvgMinPerPage(sessions),
            totalReadTime = getTotalReadTime(sessions),
            currentPage = currentPage.toString(),
            maxPage = totalPages.toString()
        )
    }
}