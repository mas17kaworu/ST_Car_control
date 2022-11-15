package com.longkai.stcarcontrol.st_exp.tracking

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.longkai.stcarcontrol.st_exp.databinding.ViewTrackPointInfoBinding

class TrackPointInfoView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewTrackPointInfoBinding.inflate(LayoutInflater.from(context), this)

    init {
        binding.expand.setOnClickListener {
            binding.detailsView.isVisible = true
            binding.expand.isVisible = false
        }
        binding.close.setOnClickListener {
            binding.detailsView.isVisible = false
            binding.expand.isVisible = true
        }
    }

    fun setData(data: TrackingData) {
        binding.apply {
            time.text = data.formatDateTime()
            location.text = data.formatLocation()
            dop.text = "0, ${data.hdop}, 0"
            gpsStatus.text = "${data.gpsStatus}"
            course.text = "${data.course}"
            speed.text = "${data.velocity}"
            error.text = data.formatError()
        }
    }

}