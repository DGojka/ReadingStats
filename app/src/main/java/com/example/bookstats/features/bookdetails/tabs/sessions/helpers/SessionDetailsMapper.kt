package com.example.bookstats.features.bookdetails.tabs.sessions.helpers

import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.repository.Session

fun List<Session>.mapToSessionDetails(sessionCalculator: SessionCalculator) =
    this.map { it.mapToSessionDetails(sessionCalculator) }

private fun Session.mapToSessionDetails(sessionCalculator: SessionCalculator) =
    sessionCalculator.calculateSessionDetails(this)