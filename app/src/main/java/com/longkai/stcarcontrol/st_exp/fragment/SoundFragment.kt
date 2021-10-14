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

    /**
     * true - 动感; false - 自然
     */
    private var soundEffect: Boolean = false

    /**
     * true - 全车; false - 前排
     */
    private var soundField: Boolean = false

    /**
     * true - ON; false - OFF
     */
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
        updateSoundEffectUI()
        updateSoundFieldUI()
        updateImmersionEffectUI()

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
        updateSoundEffectUI()
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundEffectSwitch(soundEffect),
            CommandListenerAdapter<CMDSoundEffectSwitch.Response>()
        )
    }

    private fun onSoundFieldChanged() {
        soundField = soundField.not()
        updateSoundFieldUI()
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundFieldSwitch(soundField),
            CommandListenerAdapter<CMDSoundFieldSwitch.Response>()
        )
    }

    private fun onImmersionEffectChanged() {
        immersionEffect = immersionEffect.not()
        updateImmersionEffectUI()
        ServiceManager.getInstance().sendCommandToCar(
            CMDImmersionEffectSwitch(immersionEffect),
            CommandListenerAdapter<CMDImmersionEffectSwitch.Response>()
        )
    }

    private fun updateSoundEffectUI() {
        binding.soundEffectIcon.isSelected = soundEffect
        val soundEffectModeTextResId = if (soundEffect) R.string.sound_effect_dynamic else R.string.sound_effect_natural
        binding.soundEffectModeText.setText(soundEffectModeTextResId)
    }

    private fun updateSoundFieldUI() {
        binding.soundFieldIcon.isSelected = soundField
        val soundFieldModeTextResId = if (soundField) R.string.sound_field_all else R.string.sound_field_front
        binding.soundFieldModeText.setText(soundFieldModeTextResId)
    }

    private fun updateImmersionEffectUI() {
        binding.immersionEffectIcon.isSelected = immersionEffect
        val immersionEffectTextResId = if (immersionEffect) R.string.sound_immersion_effect_on else R.string.sound_immersion_effect_off
        binding.immersionEffectModeText.setText(immersionEffectTextResId)

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