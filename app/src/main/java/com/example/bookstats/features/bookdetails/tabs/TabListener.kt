package com.example.bookstats.features.bookdetails.tabs

interface TabListener {
    interface GeneralTabListener{
        fun onSessionStart()
    }

    interface SettingsTabListener{
        fun onDeleteBook()

        fun onAddSession()

        fun onEditBook()
    }
}