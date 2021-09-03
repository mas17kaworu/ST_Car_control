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
    private var volume: Int = 10
    private var speed: Int = 0x3FFE / 2
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
        val textRes = if (play) R.string.mute else R.string.unmute
        binding.playText.setText(textRes)
    }

}