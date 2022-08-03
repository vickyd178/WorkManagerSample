package com.avion.workmanagersample.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.avion.workmanagersample.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ExpediteWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {

        withContext(Dispatchers.IO) {
            delay(5000)
        }

        println("Task Completed")
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return showForegroundNotification()
    }

    private fun showForegroundNotification(): ForegroundInfo {
        return ForegroundInfo(
            Random.nextInt(),
            NotificationCompat.Builder(context, "download_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Expedite Work is being completed")
                .setContentText("Expedite Worker in progress this notification is manually delayed to simulate work action")
                .build()
        )
    }
}