package com.longkai.stcarcontrol.st_exp.tracking

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
                hideRealTrack = binding.hideRealTrackSwitch.isChecked,
                labelInterval = binding.labelIntervalEditText.text.toString().toIntOrNull() ?: 0,
                replaySpeed = binding.replaySpeedEditText.text.toString().toIntOrNull() ?: 0
            )
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setData(hideRealTrack: Boolean, labelInterval: Int, replaySpeed: Int) {
        binding.apply {
            hideRealTrackSwitch.isChecked = hideRealTrack
            labelIntervalEditText.setText(labelInterval.toString())
            replaySpeedEditText.setText(replaySpeed.toString())
        }
    }

    interface Listener {
        fun onSaveSettings(hideRealTrack: Boolean, labelInterval: Int, replaySpeed: Int)
    }
}