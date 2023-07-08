package com.example.bookstats.features.realtimesessions.timer.helpers

interface TimerBroadcastListener {
    fun onTimerBroadcastReceiver(currentMs: Float)
}