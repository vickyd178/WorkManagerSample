package com.avion.workmanagersample.workers

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.LocalDateTime
import java.util.*

class LogWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println("From worker ${LocalDateTime.now()}")
        } else {
            println("From worker ${Calendar.getInstance().time}")

        }
        Toast.makeText(applicationContext, "15 minutes over", Toast.LENGTH_LONG).show()
        return Result.success()
    }
}