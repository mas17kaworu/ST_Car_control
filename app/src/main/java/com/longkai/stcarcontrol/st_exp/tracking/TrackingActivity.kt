package com.longkai.stcarcontrol.st_exp.tracking

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amap.api.location.AMapLocationClient
import com.google.android.material.snackbar.Snackbar
import com.longkai.stcarcontrol.st_exp.databinding.FragmentTrackingBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TrackingActivity : ComponentActivity() {

    private val viewModel: TrackingViewModel by viewModels()

    private lateinit var binding: FragmentTrackingBinding
    private val mapView get() = binding.mapView
    private lateinit var aMapHelper: AMapHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tracking.init(this)

        binding = FragmentTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);

//        MapsInitializer.setTerrainEnable(true)
        mapView.onCreate(savedInstanceState)

        aMapHelper = AMapHelper(
            context = this,
            aMap = mapView.map,
            updateTrackPointInfo = this::updateTrackPointInfo,
            showMessage = this::showSnackbar
        )

        initUI()
        initHistoryRecords()
    }

    override fun onDestroy() {
        super.onDestroy()
        aMapHelper.clearTrack()
        mapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    private fun initHistoryRecords() {
        fun hideHistoryRecordsLayout() {
            binding.historyRecordsLayout.isVisible = false
        }

        binding.historyRecordsBtn.setOnClickListener {
            lifecycleScope.launch {
                val historyRecords = viewModel.loadHistoryRecords()
                binding.historyRecords.adapter = HistoryRecordsAdapter(historyRecords) {
                    viewModel.enterReviewMode()
                    loadRecord(it)
                    hideHistoryRecordsLayout()
                }
                binding.historyRecordsLayout.apply { isVisible = isVisible.not() }
            }
        }
    }


    private fun initUI() {
        aMapHelper.init()

//        binding.moveCarBtn.setOnClickListener { moveCar() }
//        binding.clearBtn.setOnClickListener { clearTrack() }
//        binding.showTrackProgressBtn.setOnClickListener { showTrackProgress() }

        binding.signalReal.setOnClickListener { viewModel.switchRealTrack() }
        binding.signalPbox.setOnClickListener { viewModel.switchPboxTrack() }

        binding.replayBtn.setOnClickListener { aMapHelper.replayTrack() }
        binding.exitReviewBtn.setOnClickListener {
            aMapHelper.clearTrack()
            viewModel.exitReviewMode()
        }
        binding.recordBtn.setOnClickListener {
            if (viewModel.uiState.value.isRecording) {
                viewModel.stopRecording { fileName ->
                    if (fileName != null) {
                        showSnackbar("Recording successfully saved to $fileName")
                    } else {
                        showSnackbar("No data saved")
                    }
                }
            } else {
                viewModel.startRecording()
            }
        }

        aMapHelper.setupInfoWindow()

        fun updateRecordBtnUI(isRecording: Boolean) {
            binding.recordBtn.text = if (isRecording) "Stop Recording" else "Start Recording"
            val tintColor =
                if (isRecording) android.R.color.holo_red_light else android.R.color.holo_green_dark
            binding.recordBtn.compoundDrawableTintList = ColorStateList.valueOf(getColor(tintColor))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    binding.apply {
                        reviewModeBtns.isInvisible = uiState.inReviewMode.not()
                        recordBtn.isInvisible = uiState.inReviewMode
                        updateRecordBtnUI(uiState.isRecording)
                        trackPointInfo.isInvisible = uiState.inReviewMode.not()

                        signalReal.isSelected = uiState.showRealTrack
                        signalPbox.isSelected = uiState.showPboxTrack
                    }

                    if (uiState.inReviewMode && uiState.historyRecordDataRefreshed && uiState.historyRecordData != null) {
                        aMapHelper.setHistoryRecordData(uiState.historyRecordData)
                        aMapHelper.showTracks(uiState.showRealTrack, uiState.showPboxTrack)
                    }
                }
            }
        }
    }

    private fun loadRecord(historyRecord: HistoryRecord) {
        viewModel.loadRecord(historyRecord)
        viewModel.enterReviewMode()
    }

    private fun updateTrackPointInfo(trackingData: TrackingData) {
        binding.trackPointInfo.visibility = View.VISIBLE
        binding.trackPointInfo.setData(trackingData)
    }

    private fun hideTrackPointInfo() {
        binding.trackPointInfo.visibility = View.INVISIBLE
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Toast.LENGTH_SHORT).show()
    }
}