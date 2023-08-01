package com.example.bookstats.features.realtimesessions.timer

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.bookstats.R
import com.example.bookstats.activity.MainActivity
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.features.bookdetails.managers.SessionCalculator
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerServiceBinder
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerServiceHelper.Companion.STOP_SERVICE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerService : Service() {

    @Inject
    lateinit var sessionCalculator: SessionCalculator

    @Inject
    lateinit var timer: Timer

    inner class TimerServiceBinderImpl : Binder(),
        TimerServiceBinder {
        override fun pauseTimer() {
            this@TimerService.pauseTimer()
        }

        override fun resumeTimer() {
            startTimer()
        }

        override fun setTime(seconds: Float) {
            timer.setTime(seconds)
        }
    }

    fun pauseTimer() {
        timer.pause()
    }

    fun startTimer() {
        timer.start()
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(STOP_SERVICE)) {
            timer.reset()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        timer.start()
        startForeground(NOTIFICATION_ID, createNotification())
        CoroutineScope(Dispatchers.IO).launch {
            timer.flow.collect { currentMs ->
                val timerIntent = Intent(TIMER_ACTION)
                Log.e("currenttime", (currentMs / 1000).toString())
                timerIntent.putExtra(CURRENT_MS, currentMs)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(timerIntent)
                sendBroadcast(timerIntent)
            }
        }

        return START_STICKY
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra(FRAGMENT_TAG, REALTIME_SESSIONS_FRAGMENT)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, TIMER_CHANNEL)
            .setContentTitle(applicationContext.resources.getString(R.string.session))
            .setContentText(applicationContext.resources.getString(R.string.session_in_progress))
            .setSmallIcon(R.drawable.app_icon)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.dark_violet))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setSound(null)
            .setOnlyAlertOnce(true)

        return notificationBuilder.build()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(TIMER_CHANNEL, TIMER_CHANNEL, importance)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder {
        return TimerServiceBinderImpl()
    }

    companion object {
        const val TIMER_CHANNEL = "TimerChannel"
        const val CURRENT_MS = "CURRENT_MS"
        const val TIMER_ACTION = "TIMER_ACTION"
        const val FRAGMENT_TAG = "FRAGMENT_TAG"
        const val REALTIME_SESSIONS_FRAGMENT = "RealTimeSessionsFragment"
        const val NOTIFICATION_ID = 1
    }
}