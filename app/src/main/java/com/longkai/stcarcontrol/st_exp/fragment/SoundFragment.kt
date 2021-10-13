package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList.*
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList.CMDSoundVolume.SoundVolumeDirection
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.databinding.FragmentSoundBinding

class SoundFragment : Fragment() {

    private lateinit var binding: FragmentSoundBinding

    private var soundEffect: Boolean = false
    private var soundField: Boolean = false
    private var immersionEffect: Boolean = false
    private var volume: Int = (VOLUME_MAX + VOLUME_MIN) / 2
    private var play: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

        binding.soundEffectIcon.setOnClickListener { onSoundEffectChanged() }
        binding.soundFieldIcon.setOnClickListener { onSoundFieldChanged() }
        binding.immersionEffectIcon.setOnClickListener { onImmersionEffectChanged() }

        binding.volumeSlider.addOnChangeListener { _, volume, _ ->
            onVolumeChanged(volume.toInt())
        }

        binding.playIcon.setOnClickListener { onPlayChanged() }
    }

    private fun initUI() {

        binding.soundEffectIcon.isSelected = soundEffect
        binding.soundFieldIcon.isSelected = soundField
        binding.immersionEffectIcon.isSelected = immersionEffect

        binding.volumeSlider.apply {
            valueFrom = VOLUME_MIN.toFloat()
            valueTo = VOLUME_MAX.toFloat()
            stepSize = VOLUME_STEP_SIZE.toFloat()
            value = volume.toFloat()
        }

        binding.playIcon.isSelected = play

    }

    private fun onSoundEffectChanged() {
        soundEffect = soundEffect.not()
        binding.soundEffectIcon.isSelected = soundEffect
        binding.soundEffectText.isSelected = soundEffect
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundEffectSwitch(soundEffect),
            CommandListenerAdapter<CMDSoundEffectSwitch.Response>()
        )
    }

    private fun onSoundFieldChanged() {
        soundField = soundField.not()
        binding.soundFieldIcon.isSelected = soundField
        binding.soundFieldText.isSelected = soundField
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundFieldSwitch(soundField),
            CommandListenerAdapter<CMDSoundFieldSwitch.Response>()
        )
    }

    private fun onImmersionEffectChanged() {
        immersionEffect = immersionEffect.not()
        binding.immersionEffectIcon.isSelected = immersionEffect
        binding.immersionEffectText.isSelected = immersionEffect
        ServiceManager.getInstance().sendCommandToCar(
            CMDImmersionEffectSwitch(immersionEffect),
            CommandListenerAdapter<CMDImmersionEffectSwitch.Response>()
        )
    }

    private fun onVolumeChanged(newVolume: Int) {
        if (newVolume != volume) {
            val direction =
                if (newVolume > volume) SoundVolumeDirection.Up else SoundVolumeDirection.Down
            volume = newVolume
            ServiceManager.getInstance().sendCommandToCar(
                CMDSoundVolume(direction, volume),
                CommandListenerAdapter<CMDSoundVolume.Response>()
            )
        }
    }

    private fun onPlayChanged() {
        play = play.not()
        binding.playIcon.isSelected = play
        val textRes = if (play) R.string.volume_unmute else R.string.volume_mute
        binding.playText.setText(textRes)
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundPlaySwitch(play),
            CommandListenerAdapter<CMDSoundPlaySwitch.Response>()
        )
    }

    companion object {
        const val VOLUME_MIN = 0
        const val VOLUME_MAX = 16
        const val VOLUME_STEP_SIZE = 1
    }
}