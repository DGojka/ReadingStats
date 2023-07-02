package com.example.bookstats.features.realtimesessions

interface TimerBroadcastListener {
    fun onTimerBroadcastReceiver(currentMs: Float)
}