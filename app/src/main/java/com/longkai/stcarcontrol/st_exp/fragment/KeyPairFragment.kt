package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.databinding.FragmentKeyPairBinding

class KeyPairFragment : Fragment() {

  private lateinit var binding: FragmentKeyPairBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentKeyPairBinding.inflate(inflater, container, false)
    return binding.root
  }
}