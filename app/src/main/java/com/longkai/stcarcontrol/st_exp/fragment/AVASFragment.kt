package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList.CMDAvasSoundMode
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDAvasList.CMDAvasSoundMode.Mode
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.databinding.FragmentAvasBinding

class AVASFragment : Fragment() {

  private lateinit var binding: FragmentAvasBinding

  private var mode: Mode = Mode.Mode1

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

    binding.mode1Icon.setOnClickListener { onModeChanged(Mode.Mode1) }
    binding.mode2Icon.setOnClickListener { onModeChanged(Mode.Mode2) }
  }

  private fun onModeChanged(newMode: Mode) {
    if (mode != newMode) {
      mode = newMode
      ServiceManager.getInstance().sendCommandToCar(
        CMDAvasSoundMode(mode),
        CommandListenerAdapter<CMDAvasSoundMode.Response>()
      )
    }
  }

  companion object {

    const val VOLUME_MIN = 0
    const val VOLUME_MAX = 20
    const val SPEED_MIN = 0
    const val SPEED_MAX = 16382
  }
}