package com.example.bookstats.features.realtimesessions.timer

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class Timer {
    private var _counterFlow: Flow<Float>? = null
    var flow: MutableStateFlow<Float> = MutableStateFlow(0f)
    private var currentMs: Int = 0
    private var paused = true

    init {
        _counterFlow = (0..Int.MAX_VALUE)
            .asSequence()
            .asFlow()
            .map { it.toFloat() }
            .onEach {
                delay(DELAY_VALUE.toLong())
                if (!paused) {
                    currentMs += DELAY_VALUE
                    flow.emit(currentMs.toFloat())
                }
            }
        GlobalScope.launch {
            _counterFlow!!.collect {}
        }
    }

    fun start() {
        paused = false
    }

    fun pause() {
        paused = true
    }

    fun reset() {
        flow.value = 0F
        currentMs = 0
    }

    fun setTime(seconds: Float){
        flow.value = seconds
        currentMs = (seconds * 1000).toInt()
    }

    companion object {
        private const val DELAY_VALUE = 500
    }
}