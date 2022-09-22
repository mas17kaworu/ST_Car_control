package com.longkai.stcarcontrol.st_exp.tracking

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.longkai.stcarcontrol.st_exp.databinding.ViewTrackPointInfoBinding

class TrackPointInfoView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewTrackPointInfoBinding.inflate(LayoutInflater.from(context), this)

    fun setData(data: TrackingData) {
        binding.apply {
            time.text = data.utcTime.toString()
//            speed.text = data.s
            latitude.text = String.format("%.2f N", data.latitude)
            longitude.text = String.format("%.2f S", data.longitude)
        }
    }

}