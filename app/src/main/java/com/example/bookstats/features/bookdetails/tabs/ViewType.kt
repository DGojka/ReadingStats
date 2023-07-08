package com.example.bookstats.features.bookdetails.tabs

sealed class ViewType {
    object TabSettings : ViewType()
    object TabGeneral : ViewType()
    object TabSessions : ViewType()
}