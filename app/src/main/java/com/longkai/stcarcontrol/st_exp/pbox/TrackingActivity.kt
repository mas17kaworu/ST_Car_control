package com.longkai.stcarcontrol.st_exp.pbox

import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amap.api.location.AMapLocationClient
import com.google.android.material.snackbar.Snackbar
import com.longkai.stcarcontrol.st_exp.Utils.hideSoftKeyboard
import com.longkai.stcarcontrol.st_exp.activity.BaseActivity
import com.longkai.stcarcontrol.st_exp.databinding.ActivityTrackingBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TrackingActivity : BaseActivity() {

    private val viewModel: TrackingViewModel by viewModels()

    private lateinit var binding: ActivityTrackingBinding
    private val mapView get() = binding.mapView
    private lateinit var aMapHelper: AMapHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tracking.init(this)

        binding = ActivityTrackingBinding.inflate(layoutInflater)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        aMapHelper.clearAllTracks()
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        fun touchInView(ev: MotionEvent, view: View): Boolean {
            return if (view.isVisible) {
                val viewRect = Rect()
                view.getGlobalVisibleRect(viewRect)
                viewRect.contains(ev.rawX.toInt(), ev.rawY.toInt())
            } else false
        }

        if (binding.historyRecordsLayout.isVisible && !touchInView(ev, binding.historyRecordsLayout)) {
            hideHistoryRecordsLayout()
            return true
        }
        if (binding.trackSettingsView.isVisible && !touchInView(ev, binding.trackSettingsView)) {
            hideTrackSettingsView()
            return true
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    private fun hideHistoryRecordsLayout() {
        binding.historyRecordsLayout.isVisible = false
    }
    private fun hideTrackSettingsView() {
        binding.trackSettingsView.isVisible = false
    }

    private fun initUI() {
        aMapHelper.init()

//        binding.moveCarBtn.setOnClickListener { moveCar() }
//        binding.clearBtn.setOnClickListener { clearTrack() }
//        binding.showTrackProgressBtn.setOnClickListener { showTrackProgress() }

        binding.apply {
            signalReal.setOnClickListener { viewModel.switchRealTrack() }
            signalPbox.setOnClickListener { viewModel.switchPboxTrack() }

            historyRecordsBtn.setOnClickListener {
                viewModel.loadHistoryRecords { historyRecords ->
                    binding.historyRecordsRV.adapter = HistoryRecordsAdapter(historyRecords) {
                        stopRecording()
                        viewModel.enterReviewMode()
                        viewModel.loadRecord(it)
                        hideHistoryRecordsLayout()
                    }
                }
                binding.historyRecordsLayout.apply { isVisible = isVisible.not() }
            }

            settingsBtn.setOnClickListener {
                trackSettingsView.apply { isVisible = isVisible.not() }
            }
            trackSettingsView.setListener(object : TrackSettingsView.Listener {
                override fun onSaveSettings(trackSettings: TrackSettings) {
                    hideTrackSettingsView()
                    trackSettingsView.hideSoftKeyboard()
                    viewModel.saveSettings(trackSettings)
                }
            })

            replayBtn.setOnClickListener {
                viewModel.enterReplayMode()
                aMapHelper.replayTrack()
            }
            exitReviewBtn.setOnClickListener {
                aMapHelper.clearAllTracks()
                viewModel.exitReviewMode()
            }

            recordBtn.setOnClickListener {
                if (viewModel.recordingState.value.isRecording) {
                    stopRecording()
                } else {
                    aMapHelper.clearAllTracks()
                    viewModel.startRecording()
                }
            }

            replayPlayBtn.setOnClickListener {
                aMapHelper.continueReplay()
            }
            replayPauseBtn.setOnClickListener {
                aMapHelper.pauseReplay()
            }
            replayExitBtn.setOnClickListener {
                viewModel.exitReplayMode()
                aMapHelper.exitReplay()
                aMapHelper.showTracks()
            }
            replayClearBtn.setOnClickListener {
                aMapHelper.clearReplay()
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
                launch {
                    viewModel.uiState.collectLatest { uiState ->
                        binding.apply {
                            reviewModeBtns.isVisible = uiState.inReviewMode
                            replayControlBtns.isVisible = uiState.inReviewMode && uiState.inReplayMode

                            recordBtn.isVisible = uiState.inReviewMode.not()
                            trackPointInfo.isVisible = uiState.inReviewMode

                            signalReal.isVisible = uiState.trackSettings.hideRealTrackUI.not()

                            trackSettingsView.setData(uiState.trackSettings)
                        }

                        if (uiState.inReviewMode && uiState.needRefreshTrack && uiState.historyRecordData != null) {
                            viewModel.clearRefreshFlag()
                            aMapHelper.setHistoryRecordData(uiState.historyRecordData)
                            aMapHelper.setConfig(uiState.trackSettings)
                            aMapHelper.showTracks()
                        }
                    }
                }
                launch {
                    viewModel.recordingState.collectLatest { state ->
                        updateRecordBtnUI(state.isRecording)
                        if (state.isRecording && state.recordingPoint != null) {
                            aMapHelper.updateRecording(state.recordingPoint)
                        }
                    }
                }
                launch {
                    viewModel.showRealTrack.collectLatest { showRealTrack ->
                        binding.signalReal.isSelected = showRealTrack
                        aMapHelper.switchRealTrackVisibility(showRealTrack)
                    }
                }
                launch {
                    viewModel.showPboxTrack.collectLatest { showPboxTrack ->
                        binding.signalPbox.isSelected = showPboxTrack
                        aMapHelper.switchPboxTrackVisibility(showPboxTrack)
                    }
                }
            }
        }
    }

    private fun stopRecording() {
        viewModel.stopRecording { fileName ->
            if (fileName != null) {
                showSnackbar("Recording successfully saved to $fileName")
            } else {
                showSnackbar("No data saved")
            }
        }
    }

    private fun updateTrackPointInfo(trackingData: TrackingData) {
        binding.trackPointInfo.visibility = View.VISIBLE
        binding.trackPointInfo.setData(trackingData)
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Toast.LENGTH_SHORT).show()
    }
}