package com.example.bookstats.features.realtimesessions

import android.app.ActivityManager
import android.content.*
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import javax.inject.Inject

class TimerServiceHelper @Inject constructor(
    private val context: Context
) {
    private lateinit var timerUpdateReceiver: BroadcastReceiver
    private lateinit var binder : TimerService.TimerServiceBinder

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as TimerService.TimerServiceBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    fun pause(){
        binder.pauseTimer()
    }

    fun resume(){
        binder.resumeTimer()
    }

    fun registerTimerUpdateReceiver(receiver: BroadcastReceiver) {
        if(!isServiceRunning()){
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

    fun stopService(){
        val intent = Intent(context, TimerService::class.java)
        intent.action = "STOP_SERVICE"
        context.unbindService(serviceConnection)
        context.stopService(intent)
    }

    fun unregisterTimerUpdateReceiver(receiver: BroadcastReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }

    private fun startService(){
        val intent = Intent(context, TimerService::class.java)
        context.startForegroundService(intent)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }


    companion object {
        const val TIMER_UPDATE_ACTION = "TIMER_ACTION"
    }
}
