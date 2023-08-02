package com.example.bookstats.features.realtimesessions.timer.helpers

import android.app.ActivityManager
import android.content.*
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.bookstats.features.realtimesessions.timer.TimerService
import javax.inject.Inject

class TimerServiceHelper @Inject constructor(
    private val context: Context
) {
    private lateinit var timerUpdateReceiver: BroadcastReceiver
    private lateinit var binder: TimerService.TimerServiceBinderImpl

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as TimerService.TimerServiceBinderImpl
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    fun pause() {
        binder.pauseTimer()
    }

    fun resume() {
        binder.resumeTimer()
    }

    fun registerTimerUpdateReceiver(receiver: BroadcastReceiver) {
        if (!isServiceRunning()) {
            startService()
        }
        timerUpdateReceiver = receiver
        val filter = IntentFilter(TIMER_UPDATE_ACTION)
        LocalBroadcastManager.getInstance(context).registerReceiver(timerUpdateReceiver, filter)
    }

    fun isServiceRunning(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

        for (serviceInfo in runningServices) {
            if (TimerService::class.java.name == serviceInfo.service.className) {
                return true
            }
        }

        return false
    }

    fun stopService() {
        val intent = Intent(context, TimerService::class.java)
        intent.action = STOP_SERVICE
        context.unbindService(serviceConnection)
        context.stopService(intent)
    }

    fun unregisterTimerUpdateReceiver(receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }

    private fun startService() {
        val intent = Intent(context, TimerService::class.java)
        context.startForegroundService(intent)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    @Throws(UninitializedPropertyAccessException::class)
    fun setTime(timeElapsedSeconds: Float) {
        binder.setTime(timeElapsedSeconds)
    }


    companion object {
        const val TIMER_UPDATE_ACTION = "TIMER_ACTION"
        const val STOP_SERVICE = "STOP_SERVICE"
    }
}
