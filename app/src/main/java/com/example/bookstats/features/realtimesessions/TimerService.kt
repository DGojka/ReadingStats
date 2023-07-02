package com.example.bookstats.features.realtimesessions

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimerService : Service() {
    private var timer: Timer? = null

    private val channelId = "TimerChannel"
    private val notificationId = 1

    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(applicationContext)
    }

    @Inject
    lateinit var context: Context

    interface MyServiceInterface {
        fun pauseTimer()
        fun resumeTimer()
    }

    inner class TimerServiceBinder : Binder(), MyServiceInterface {
        override fun pauseTimer() {
            stopTimer()
        }

        override fun resumeTimer() {
            startTimer()
        }
    }

    fun stopTimer(){
        timer!!.pause()
    }

    fun startTimer(){
        timer!!.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer = Timer()
        timer!!.start()
        CoroutineScope(Dispatchers.IO).launch {
            timer!!.flow.collect { currentMs ->
                val timerIntent = Intent("TIMER_ACTION")
                timerIntent.putExtra("CURRENT_MS", currentMs)
                startForeground(notificationId, createNotification(currentMs))
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
        notificationIntent.putExtra("FRAGMENT_TAG", "RealTimeSessionFragment")
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Timer Service")
            .setContentText("Current Time: $currentMs")
            .setSmallIcon(R.drawable.app_icon)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(this, R.color.dark_violet))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setSound(null) // Wyłącz dźwięk notyfikacji
            .setOnlyAlertOnce(true) // Wyświetl powiadomienie tylko raz

        return notificationBuilder!!.build()
    }

    private fun updateNotification(currentMs: Float) {
        notificationBuilder?.setContentText("Current Time: $currentMs")
        notificationManager.notify(notificationId, notificationBuilder?.build()!!)
    }

    private fun createNotificationChannel() {
        val channelName = "Timer Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        timer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        return TimerServiceBinder()
    }
}