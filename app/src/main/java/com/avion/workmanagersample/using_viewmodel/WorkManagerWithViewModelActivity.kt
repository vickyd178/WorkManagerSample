package com.avion.workmanagersample.using_viewmodel

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.avion.workmanagersample.databinding.ActivityMainBinding
import com.avion.workmanagersample.workers.WorkerKeys

class WorkManagerWithViewModelActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<WorkerViewModel>()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonStartDownload.setOnClickListener {
            viewModel.beginWork()
        }

        viewModel.workManagerInfos.observe(this, observeWorkInfo())
        viewModel.imageStringUriLiveData.observe(this, observeImageURIs())


        viewModel.expediteWorkInfo.observe(this, observeExpediteWork())
        viewModel.periodicWorkInfo.observe(this, observePeriodicWork())

        binding.buttonExpediteWork.setOnClickListener {
            viewModel.beginExpediteWork()
        }

        binding.buttonStartPeriodicWork.setOnClickListener {
            viewModel.beginPeriodicWork()
        }
    }

    private fun observePeriodicWork(): Observer<MutableList<WorkInfo>> {
        return Observer { listOfWorkInfos ->
            listOfWorkInfos?.let {
                val periodicWorkInfo = listOfWorkInfos.find {
                    it.tags == viewModel.loggingWorker.tags
                }
                setPeriodicWorkInfoText(periodicWorkInfo?.state)
            }
        }

    }

    private fun observeExpediteWork(): Observer<MutableList<WorkInfo>> {
        return Observer { listOfWorkInfos ->
            listOfWorkInfos?.let {
                val expediteWorkInfo = listOfWorkInfos.find {
                    it.tags == viewModel.expediteWorker.tags
                }

                setExpediteWorkInfoText(expediteWorkInfo?.state)
            }

        }
    }

    private fun setExpediteWorkInfoText(state: WorkInfo.State?) {
        binding.tvExpediteWorkState.text = state?.name
    }

    private fun setPeriodicWorkInfoText(state: WorkInfo.State?) {
        binding.tvPeriodicState.text = state?.name
    }

    private fun observeImageURIs(): Observer<in String> {
        return Observer {
            it?.let {
                binding.imageView2.setImageURI(it.toUri())
            }
        }
    }

    private fun observeWorkInfo(): Observer<MutableList<WorkInfo>> {
        return Observer { listOfWorkerInfo ->
            if (listOfWorkerInfo.isNullOrEmpty()) {
                return@Observer
            }
            val downloadInfo = listOfWorkerInfo.find {
                it.tags == viewModel.downloadRequest.tags
            }
            val filterInfo = listOfWorkerInfo.find {
                it.tags == viewModel.colorFilterRequest.tags
            }

            val downloadedImageUri = downloadInfo?.outputData?.getString(WorkerKeys.IMAGE_URI)
            val filteredImageUri = filterInfo?.outputData?.getString(WorkerKeys.FILTER_URI)

            viewModel.setImageUriString(filteredImageUri ?: downloadedImageUri)
            binding.buttonStartDownload.isEnabled = downloadInfo?.state != WorkInfo.State.RUNNING
            setTextViewStates(downloadInfo?.state, filterInfo?.state)
        }
    }

    private fun setTextViewStates(downloadState: WorkInfo.State?, filterState: WorkInfo.State?) {
        when (downloadState) {
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
        when (filterState) {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}