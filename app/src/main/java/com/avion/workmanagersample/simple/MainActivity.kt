package com.avion.workmanagersample.simple

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.avion.workmanagersample.workers.ColorFilterWorker
import com.avion.workmanagersample.workers.DownloadWorker
import com.avion.workmanagersample.workers.WorkerKeys
import com.avion.workmanagersample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()

        val colorFilterRequest = OneTimeWorkRequestBuilder<ColorFilterWorker>()
            .build()

        val workManager = WorkManager.getInstance(applicationContext)

        val uriLiveData = MutableLiveData<String>()
        var downloadInfo: WorkInfo? = null
        var filterInfo: WorkInfo? = null

        workManager.getWorkInfosForUniqueWorkLiveData("download")
            .observe(this) { _allWorkInfo ->
                _allWorkInfo?.let { allWorkInfo ->
                    downloadInfo = allWorkInfo.find {
                        it.id == downloadRequest.id
                    }

                    filterInfo = allWorkInfo.find {
                        it.id == colorFilterRequest.id
                    }

                    val downloadUri = downloadInfo?.outputData?.getString(WorkerKeys.IMAGE_URI)
                    val filterUri = filterInfo?.outputData?.getString(WorkerKeys.FILTER_URI)
                    uriLiveData.value = filterUri ?: downloadUri

                    when (downloadInfo?.state) {
                        WorkInfo.State.RUNNING -> {
                            binding.tvDownloadStatus.text = "Downloading..."
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            binding.tvDownloadStatus.text = "Download succeeded"
                        }
                        WorkInfo.State.FAILED -> {
                            binding.tvDownloadStatus.text = "Download failed"
                        }
                        WorkInfo.State.CANCELLED -> {
                            binding.tvDownloadStatus.text = "Download cancelled"
                        }
                        WorkInfo.State.ENQUEUED -> {
                            binding.tvDownloadStatus.text = "Download enqueued"
                        }
                        WorkInfo.State.BLOCKED -> {
                            binding.tvDownloadStatus.text = "Download blocked"
                        }
                        else -> {}
                    }
                    when (filterInfo?.state) {
                        WorkInfo.State.RUNNING -> {
                            binding.tvFilterStatus.text = "Applying filter..."
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            binding.tvFilterStatus.text = "Filter succeeded"
                        }
                        WorkInfo.State.FAILED -> {
                            binding.tvFilterStatus.text = "Filter failed"
                        }
                        WorkInfo.State.CANCELLED -> {
                            binding.tvFilterStatus.text = "Filter cancelled"
                        }
                        WorkInfo.State.ENQUEUED -> {
                            binding.tvFilterStatus.text = "Filter enqueued"
                        }
                        WorkInfo.State.BLOCKED -> {
                            binding.tvFilterStatus.text = "Filter blocked"
                        }
                        else -> {}
                    }
                }

                binding.buttonStartDownload.isEnabled = downloadInfo?.state != WorkInfo.State.RUNNING

            }

        binding.buttonStartDownload.setOnClickListener {
            workManager.beginUniqueWork(
                "download",
                ExistingWorkPolicy.KEEP,
                downloadRequest
            ).then(colorFilterRequest).enqueue()
        }
        uriLiveData.observe(this) {
            it?.let {
                binding.imageView2.setImageURI(it.toUri())
            }
        }

    }
}