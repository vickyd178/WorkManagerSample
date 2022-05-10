package com.avion.workmanagersample.using_viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.avion.workmanagersample.workers.ColorFilterWorker
import com.avion.workmanagersample.workers.DownloadWorker
import com.avion.workmanagersample.workers.LogWorker
import java.util.concurrent.TimeUnit

class WorkerViewModel(application: Application) : AndroidViewModel(application) {
    private val workManager: WorkManager = WorkManager.getInstance(application)
    val downloadRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()

    val colorFilterRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<ColorFilterWorker>()
        .build()

    val loggingWorker: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<LogWorker>(15 * 60 * 1000L, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).build()

    private val _imageStringUriMutableLiveData = MutableLiveData<String>()
    val imageStringUriLiveData get() :LiveData<String> = _imageStringUriMutableLiveData

    fun setImageUriString(uriString: String?) {
        uriString.let {
            _imageStringUriMutableLiveData.value = it
        }
    }


    val workManagerInfos = workManager.getWorkInfosForUniqueWorkLiveData("download")

    fun beginWork() {
        workManager.beginUniqueWork("download", ExistingWorkPolicy.KEEP, downloadRequest)
            .then(colorFilterRequest).enqueue()
    }

    fun beginPeriodicWork(): LiveData<Operation.State> {
        return workManager.enqueue(loggingWorker).state
    }

}