package com.longkai.stcarcontrol.st_exp.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButtonToggleGroup
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList.*
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDSoundList.CMDSoundVolume.SoundVolumeDirection
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.databinding.FragmentSoundBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SoundFragment : Fragment() {

    private lateinit var binding: FragmentSoundBinding

    private var soundEffect: SoundEffect = SoundEffect.Cozy
    private var soundField: SoundField = SoundField.Quality
    private var immersionEffect: ImmersionEffect = ImmersionEffect.Natural
    private var soundMode: SoundMode = SoundMode.On
    private var soundStyle: SoundStyle? = null

    private var volume: Int = (VOLUME_MAX + VOLUME_MIN) / 2
    private var play: Boolean = false

    private val handler = Handler(Looper.getMainLooper())

    enum class SoundStyle {
        Hifi, Concert, Cinema;

        companion object  {
            fun withName(name: String): SoundStyle? {
                return SoundStyle.values().find { it.name == name }
            }
        }
    }

    private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("sound_fragment")
    private val SOUND_STYLE = stringPreferencesKey("sound_style")

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

        binding.soundEffectToggleButtonGroup.addOnButtonCheckedListener(toggleButtonCheckedListener)
        binding.soundFieldToggleButtonGroup.addOnButtonCheckedListener(toggleButtonCheckedListener)
        binding.immersionEffectToggleButtonGroup.addOnButtonCheckedListener(toggleButtonCheckedListener)

        binding.soundModeOnOff.setOnClickListener {
            val newSoundMode = if (soundMode == SoundMode.On) SoundMode.Off else SoundMode.On
            onSoundModeChanged(newSoundMode)
        }

        binding.soundStyleHifi.setOnClickListener {
            setSoundStyle(SoundStyle.Hifi)
        }
        binding.soundStyleConcert.setOnClickListener {
            setSoundStyle(SoundStyle.Concert)
        }
        binding.soundStyleCinema.setOnClickListener {
            setSoundStyle(SoundStyle.Cinema)
        }

        binding.volumeSlider.addOnChangeListener { _, volume, _ ->
            onVolumeChanged(volume.toInt())
        }

        binding.volumeOnOff.setOnClickListener { onPlayChanged() }

        readPersistedSoundStyle()
    }

    private val toggleButtonCheckedListener = object : MaterialButtonToggleGroup.OnButtonCheckedListener {
        override fun onButtonChecked(
            group: MaterialButtonToggleGroup?,
            checkedId: Int,
            isChecked: Boolean
        ) {
            if (!isChecked) return
            when (checkedId) {
                binding.soundEffectCozy.id -> onSoundEffectChanged(SoundEffect.Cozy)
                binding.soundEffectDynamic.id -> onSoundEffectChanged(SoundEffect.Dynamic)
                binding.soundFieldQuality.id -> onSoundFieldChanged(SoundField.Quality)
                binding.soundFieldFocus.id -> onSoundFieldChanged(SoundField.Focus)
                binding.immersionEffectNatural.id -> onImmersionEffectChanged(ImmersionEffect.Natural)
                binding.immersionEffectSurround.id -> onImmersionEffectChanged(ImmersionEffect.Surround)
            }
        }
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

        binding.volumeOnOff.isSelected = play

    }

    private fun onSoundEffectChanged(newSoundEffect: SoundEffect) {
        soundEffect = newSoundEffect
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundEffectSwitch(soundEffect),
            CommandListenerAdapter<CMDSoundEffectSwitch.Response>()
        )
    }

    private fun onSoundFieldChanged(newSoundField: SoundField) {
        soundField = newSoundField
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundFieldSwitch(soundField),
            CommandListenerAdapter<CMDSoundFieldSwitch.Response>()
        )
    }

    private fun onImmersionEffectChanged(newImmersionEffect: ImmersionEffect) {
        immersionEffect = newImmersionEffect
        ServiceManager.getInstance().sendCommandToCar(
            CMDImmersionEffectSwitch(immersionEffect),
            CommandListenerAdapter<CMDImmersionEffectSwitch.Response>()
        )
    }

    private fun updateSoundEffectUI() {
        binding.soundEffectToggleButtonGroup.check(
            if (soundEffect == SoundEffect.Cozy) binding.soundEffectCozy.id else binding.soundEffectDynamic.id
        )
    }

    private fun updateSoundFieldUI() {
        binding.soundFieldToggleButtonGroup.check(
            if (soundField == SoundField.Quality) binding.soundFieldQuality.id else binding.soundFieldFocus.id
        )
    }

    private fun updateImmersionEffectUI() {
        binding.immersionEffectToggleButtonGroup.check(
            if (immersionEffect == ImmersionEffect.Natural) binding.immersionEffectNatural.id else binding.immersionEffectSurround.id
        )
    }

    private fun onSoundModeChanged(newSoundMode: SoundMode) {
        soundMode = newSoundMode
        binding.soundModeOnOff.setText(if(soundMode == SoundMode.On) R.string.sound_mode_turn_off else R.string.sound_mode_turn_on)
        val enabled = (soundMode == SoundMode.On)
        binding.soundModeGroup.referencedIds.map {
            val view = binding.root.findViewById<View>(it)
            if (view is MaterialButtonToggleGroup) {
                view.enableChildren(enabled)
            } else {
                view.isEnabled = enabled
            }
        }
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundModeSwitch(soundMode),
            CommandListenerAdapter<CMDSoundModeSwitch.Response>()
        )
    }

    private fun MaterialButtonToggleGroup.enableChildren(enabled: Boolean) {
        for (i in 0 until childCount) {
            getChildAt(i).isEnabled = enabled
        }
    }

    private fun setSoundStyle(newSoundStyle: SoundStyle?) {
        soundStyle = newSoundStyle
        when(newSoundStyle) {
            SoundStyle.Hifi -> {
                soundEffect = SoundEffect.Cozy
                soundField = SoundField.Quality
                immersionEffect = ImmersionEffect.Natural
            }
            SoundStyle.Concert -> {
                soundEffect = SoundEffect.Cozy
                soundField = SoundField.Quality
                immersionEffect = ImmersionEffect.Surround
            }
            SoundStyle.Cinema -> {
                soundEffect = SoundEffect.Dynamic
                soundField = SoundField.Focus
                immersionEffect = ImmersionEffect.Surround
            }
            null -> {
                soundEffect = SoundEffect.Cozy
                soundField = SoundField.Quality
                immersionEffect = ImmersionEffect.Natural
            }
        }

        updateSoundEffectUI()
        updateSoundFieldUI()
        updateImmersionEffectUI()

        binding.soundStyleHifi.isSelected = (newSoundStyle == SoundStyle.Hifi)
        binding.soundStyleConcert.isSelected = (newSoundStyle == SoundStyle.Concert)
        binding.soundStyleCinema.isSelected = (newSoundStyle == SoundStyle.Cinema)

        ServiceManager.getInstance().sendCommandToCar(
            CMDAkmSound(soundEffect, soundField, immersionEffect),
            CommandListenerAdapter<CMDSoundVolume.Response>()
        )

        persistSoundStyle(newSoundStyle)
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
        binding.volumeOnOff.isSelected = play
        val textRes = if (play) R.string.volume_unmute else R.string.volume_mute
        binding.playText.setText(textRes)
        ServiceManager.getInstance().sendCommandToCar(
            CMDSoundPlaySwitch(play),
            CommandListenerAdapter<CMDSoundPlaySwitch.Response>()
        )
    }

    private fun readPersistedSoundStyle() {
        viewLifecycleOwner.lifecycleScope.launch {
            requireContext().dataStore.data.collect { prefs ->
                val savedSoundStyle = prefs.get(SOUND_STYLE)
                savedSoundStyle?.let {
                    soundStyle = SoundStyle.withName(it)
                    setSoundStyle(soundStyle)
                }
            }
        }
    }

    private fun persistSoundStyle(newSoundStyle: SoundStyle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            requireContext().dataStore.edit { prefs ->
                if (newSoundStyle != null) {
                    prefs.set(SOUND_STYLE, newSoundStyle.name)
                } else {
                    prefs.remove(SOUND_STYLE)
                }
            }
        }
    }

    companion object {
        const val VOLUME_MIN = 0
        const val VOLUME_MAX = 16
        const val VOLUME_STEP_SIZE = 1
    }
}