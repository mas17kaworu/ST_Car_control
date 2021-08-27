package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.databinding.FragmentSoundBinding

class SoundFragment : Fragment() {

  private lateinit var binding: FragmentSoundBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    binding = FragmentSoundBinding.inflate(inflater, container, false)
    return binding.root
  }
}