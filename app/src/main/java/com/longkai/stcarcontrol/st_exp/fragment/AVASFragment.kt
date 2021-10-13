package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList.*
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList.CMDAvasVolume.AvasVolumeDirection
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.databinding.FragmentAvasBinding

class AVASFragment : Fragment() {

    private lateinit var binding: FragmentAvasBinding

    private var mode: Mode = Mode.Mode1
    private var volume: Int = (VOLUME_MAX + VOLUME_MIN) / 2
    private var speed: Int = (SPEED_MAX + SPEED_MIN) / 2
    private var play: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAvasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateModeUI()
        updateMuteUI()
        initSliders()

        binding.mode1Icon.setOnClickListener { onModeChanged(Mode.Mode1) }
        binding.mode2Icon.setOnClickListener { onModeChanged(Mode.Mode2) }

        binding.volumeSlider.addOnChangeListener { _, volume, _ ->
            onVolumeChanged(volume.toInt())
        }

        binding.speedSlider.addOnChangeListener { _, speed, _ ->
            onSpeedChanged(speed.toInt())
        }

        binding.playIcon.setOnClickListener { onPlayChanged() }
    }

    private fun onModeChanged(newMode: Mode) {
        if (mode != newMode) {
            mode = newMode
            updateModeUI()
            ServiceManager.getInstance().sendCommandToCar(
                CMDAvasSoundMode(mode),
                CommandListenerAdapter<CMDAvasSoundMode.Response>()
            )
        }
    }

    private fun updateModeUI() {
        binding.mode1Icon.isSelected = mode == Mode.Mode1
        binding.mode1Text.isSelected = mode == Mode.Mode1

        binding.mode2Icon.isSelected = mode == Mode.Mode2
        binding.mode2Text.isSelected = mode == Mode.Mode2
    }

    private fun onVolumeChanged(newVolume: Int) {
        if (newVolume != volume) {
            val direction =
                if (newVolume > volume) AvasVolumeDirection.Up else AvasVolumeDirection.Down
            volume = newVolume
            ServiceManager.getInstance().sendCommandToCar(
                CMDAvasVolume(direction, volume),
                CommandListenerAdapter<CMDAvasVolume.Response>()
            )
        }
    }

    private fun onSpeedChanged(newSpeed: Int) {
        if (newSpeed != speed) {
            speed = newSpeed
            ServiceManager.getInstance().sendCommandToCar(
                CMDAvasSpeed(speed),
                CommandListenerAdapter<CMDAvasSpeed.Response>()
            )
        }
    }

    private fun onPlayChanged() {
        play = play.not()
        updateMuteUI()
        ServiceManager.getInstance().sendCommandToCar(
            CMDAvasSoundSwitch(mode, play),
            CommandListenerAdapter<CMDAvasSoundSwitch.Response>()
        )
    }

    private fun updateMuteUI() {
        binding.playIcon.isSelected = play
        val textRes = if (play) R.string.volume_unmute else R.string.volume_mute
        binding.playText.setText(textRes)
    }

    private fun initSliders() {
        binding.volumeSlider.apply {
            valueFrom = VOLUME_MIN.toFloat()
            valueTo = VOLUME_MAX.toFloat()
            stepSize = VOLUME_STEP_SIZE.toFloat()
            value = volume.toFloat()
        }
        binding.speedSlider.apply {
            valueFrom = SPEED_MIN.toFloat()
            valueTo = SPEED_MAX.toFloat()
            stepSize = SPEED_STEP_SIZE.toFloat()
            value = speed.toFloat()
        }
    }

    companion object {
        const val VOLUME_MIN = 0
        const val VOLUME_MAX = 20
        const val VOLUME_STEP_SIZE = 1
        const val SPEED_MIN = 0
        const val SPEED_MAX = 0x3FFE
        const val SPEED_STEP_SIZE = 1
    }

}