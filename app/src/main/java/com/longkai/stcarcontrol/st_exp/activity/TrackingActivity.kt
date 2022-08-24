package com.longkai.stcarcontrol.st_exp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.amap.api.location.AMapLocationClient
import com.longkai.stcarcontrol.st_exp.databinding.FragmentTrackingBinding
import com.longkai.stcarcontrol.st_exp.tracking.Tracking

class TrackingActivity : ComponentActivity() {

    private lateinit var binding: FragmentTrackingBinding
    private val mapView get() = binding.mapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
        mapView.onCreate(savedInstanceState)

        Tracking.init(this)
    }

    override fun onDestroy() {
        super.onDestroy()
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
}