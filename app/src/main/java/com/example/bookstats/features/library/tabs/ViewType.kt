package com.example.bookstats.features.library.tabs

sealed class ViewType {
    object TabSettings : ViewType()
    object TabGeneral : ViewType()
    object TabSessions : ViewType()
}