package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList.*
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList.*
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList.CMDSoundVolume.SoundVolumeDirection
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.databinding.FragmentSoundBinding

class SoundFragment : Fragment() {

    private lateinit var binding: FragmentSoundBinding

    private var soundEffect: Boolean = false
    private var soundField: Boolean = false
    private var immersionEffect: Boolean = false
    private var volume: Int = 10
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

        binding.soundEffectIcon.setOnClickListener { onSoundEffectChanged() }
        binding.soundFieldIcon.setOnClickListener { onSoundFieldChanged() }
        binding.immersionEffectIcon.setOnClickListener { onImmersionEffectChanged() }

        binding.volumeSlider.addOnChangeListener { _, volume, _ ->
            onVolumeChanged(volume.toInt())
        }

        binding.playIcon.setOnClickListener { onPlayChanged() }
    }

    private fun onSoundEffectChanged() {
        soundEffect = soundEffect.not()
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundEffectSwitch(soundEffect),
            CommandListenerAdapter<CMDSoundEffectSwitch.Response>()
        )
    }

    private fun onSoundFieldChanged() {
        soundField = soundField.not()
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundFieldSwitch(soundField),
            CommandListenerAdapter<CMDSoundFieldSwitch.Response>()
        )
    }

    private fun onImmersionEffectChanged() {
        immersionEffect = immersionEffect.not()
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
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundPlaySwitch(play),
            CommandListenerAdapter<CMDSoundPlaySwitch.Response>()
        )
    }
}