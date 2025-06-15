package com.example.fitly.model.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.fitly.R
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        showNotification()

        // Отримуємо збережені часи запуску з Data (передані при плануванні воркера)
        val hour = inputData.getInt("hour", 9)
        val minute = inputData.getInt("minute", 0)

        // Переплановуємо на наступний запуск у той самий час
        scheduleNextWork(applicationContext, hour, minute)

        return Result.success()
    }

    private fun showNotification() {
        val channelId = "daily_reminder"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(applicationContext.getString(R.string.workout_reminder))
            .setContentText(applicationContext.getString(R.string.dont_forget_to_complete_your_workout_today))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {

        fun scheduleFirstWork(context: Context, hour: Int, minute: Int) {
            val delay = calculateDelayUntilNextTime(hour, minute)

            val inputData = workDataOf(
                "hour" to hour,
                "minute" to minute
            )

            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "daily_notification",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }

        private fun scheduleNextWork(context: Context, hour: Int, minute: Int) {
            val delay = calculateDelayUntilNextTime(hour, minute)

            val inputData = workDataOf(
                "hour" to hour,
                "minute" to minute
            )

            val nextWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "daily_notification",
                ExistingWorkPolicy.REPLACE,
                nextWorkRequest
            )
        }

        private fun calculateDelayUntilNextTime(hour: Int, minute: Int): Long {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (target.before(now)) {
                target.add(Calendar.DAY_OF_MONTH, 1)
            }
            return target.timeInMillis - now.timeInMillis
        }
    }
}
