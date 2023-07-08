package com.example.bookstats.features.realtimesessions.timer

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.bookstats.R
import com.example.bookstats.activity.MainActivity
import com.example.bookstats.app.ReadingStatsApp
import com.example.bookstats.features.library.managers.SessionCalculator
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerServiceHelper.Companion.STOP_SERVICE
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerServiceBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerService : Service() {

    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(applicationContext)
    }

    @Inject
    lateinit var sessionCalculator: SessionCalculator

    @Inject
    lateinit var timer: Timer

    inner class TimerServiceBinderImpl : Binder(),
        TimerServiceBinder {
        override fun pauseTimer() {
            stopTimer()
        }

        override fun resumeTimer() {
            startTimer()
        }
    }

    fun stopTimer() {
        timer.pause()
    }

    fun startTimer() {
        timer.start()
    }

    override fun onCreate() {
        super.onCreate()
        (application as ReadingStatsApp).appComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(STOP_SERVICE)) {
            timer.reset()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        timer = Timer()
        timer.start()
        CoroutineScope(Dispatchers.IO).launch {
            timer.flow.collect { currentMs ->
                val timerIntent = Intent(TIMER_ACTION)
                timerIntent.putExtra(CURRENT_MS, currentMs)
                startForeground(NOTIFICATION_ID, createNotification(currentMs))
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(timerIntent)
                sendBroadcast(timerIntent)
                updateNotification(currentMs)
            }
        }

        return START_STICKY
    }

    private fun createNotification(currentMs: Float): Notification {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra(FRAGMENT_TAG, REALTIME_SESSIONS_FRAGMENT)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder = NotificationCompat.Builder(this, TIMER_CHANNEL)
            .setContentTitle(SESSION)
            .setContentText(CURRENT_TIME + sessionCalculator.getHourMinAndSec(currentMs = currentMs))
            .setSmallIcon(R.drawable.app_icon)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.dark_violet))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setSound(null)
            .setOnlyAlertOnce(true)

        return notificationBuilder!!.build()
    }

    private fun updateNotification(currentMs: Float) {
        notificationBuilder?.setContentText(CURRENT_TIME + sessionCalculator.getHourMinAndSec(currentMs = currentMs))
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder?.build()!!)
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
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
        const val SESSION = "Session"
        const val CURRENT_TIME = "Current Time: "
        const val NOTIFICATION_ID = 1
    }
}