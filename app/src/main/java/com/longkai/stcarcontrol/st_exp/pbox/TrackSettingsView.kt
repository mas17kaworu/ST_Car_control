package com.longkai.stcarcontrol.st_exp.pbox

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.longkai.stcarcontrol.st_exp.databinding.ViewTrackSettingsBinding

class TrackSettingsView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    private val binding = ViewTrackSettingsBinding.inflate(LayoutInflater.from(context), this, true)

    private var listener: Listener? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        binding.saveBtn.setOnClickListener {
            listener?.onSaveSettings(
                TrackSettings(
                    hideRealTrackUI = binding.hideRealTrackSwitch.isChecked,
                    labelInterval = binding.labelIntervalEditText.text.toString().toIntOrNull() ?: 0,
                    replaySpeed = binding.replaySpeedEditText.text.toString().toIntOrNull() ?: 0,
                    replayCameraFollowCar = binding.replayCameraFollowCarSwitch.isChecked,
                    showRecordingLogs = binding.showRecordingLogsSwitch.isChecked,
                    showPPSAlarm = binding.showPpsAlarmSwitch.isChecked,
                    showAntennaAlarm = binding.showAntennaAlarmSwitch.isChecked,
                    showWBIAlarm = binding.showWbiAlarmSwitch.isChecked,
                    showNBIAlarm = binding.showNbiAlarmSwitch.isChecked,
                    showSpoofingAlarm = binding.showSpoofingAlarmSwitch.isChecked
                )
            )
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setData(trackSettings: TrackSettings) {
        binding.apply {
            hideRealTrackSwitch.isChecked = trackSettings.hideRealTrackUI
            labelIntervalEditText.setText(trackSettings.labelInterval.toString())
            replaySpeedEditText.setText(trackSettings.replaySpeed.toString())
            replayCameraFollowCarSwitch.isChecked = trackSettings.replayCameraFollowCar
            showRecordingLogsSwitch.isChecked = trackSettings.showRecordingLogs
            showPpsAlarmSwitch.isChecked = trackSettings.showPPSAlarm
            showAntennaAlarmSwitch.isChecked = trackSettings.showAntennaAlarm
            showWbiAlarmSwitch.isChecked = trackSettings.showWBIAlarm
            showNbiAlarmSwitch.isChecked = trackSettings.showNBIAlarm
            showSpoofingAlarmSwitch.isChecked = trackSettings.showSpoofingAlarm
        }
    }

    interface Listener {
        fun onSaveSettings(trackSettings: TrackSettings)
    }
}