package com.avion.workmanagersample.workers

import android.app.Notification
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.avion.workmanagersample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

class LogWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        setForegroundAsync(getForegroundInfo())
        withContext(Dispatchers.IO) {
            delay(5000L)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                println("From worker ${LocalDateTime.now()}")
            } else {
                println("From worker ${Calendar.getInstance().time}")
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "10 minutes over", Toast.LENGTH_LONG).show()
            }

        }
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {

        return ForegroundInfo(Random.nextInt(), createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(context, "download_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Logger")
            .setContentText("Logger is Running")
            .build()
    }
}