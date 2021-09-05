package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList.CMDKeyPairCancel
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList.CMDKeyPairStart
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.databinding.FragmentKeyPairBinding
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl
import kotlin.random.Random

class KeyPairFragment : Fragment() {

    enum class PairStep {
        Home, Start, Pairing, Success, Failed
    }

    private lateinit var binding: FragmentKeyPairBinding
    private val handler = Handler(Looper.getMainLooper())

    private var step: PairStep = PairStep.Home
        set(value) {
            field = value
            when (value) {
                PairStep.Home, PairStep.Success, PairStep.Failed -> keys.clear()
                else -> {}
            }

            handler.post { updateUI() }
        }
    private val keys: MutableList<Int> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKeyPairBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                step = PairStep.Home
                ServiceManager.getInstance().sendCommandToCar(
                    CMDKeyPairCancel(),
                    CommandListenerAdapter<CMDKeyPairCancel.Response>()
                )
            }
        })

        binding.keyIcon.setOnClickListener {
            if (step != PairStep.Home) return@setOnClickListener

            keys.clear()
            keys.addAll(generateKeys())
            step = PairStep.Start
            updateUI()

            ServiceManager.getInstance().sendCommandToCar(
                CMDKeyPairStart(keys),
                object : CommandListenerAdapter<CMDKeyPairStart.Response>(TIMEOUT_MS) {
                    override fun onSuccess(response: CMDKeyPairStart.Response?) {
                        println("zcf onSuccess")
                        when (response?.status) {
                            CMDKeyPairStart.Response.STATUS_PAIR_IN_PROGRESS -> step = PairStep.Pairing
                            CMDKeyPairStart.Response.STATUS_PAIR_SUCCESS -> step = PairStep.Success
                            CMDKeyPairStart.Response.STATUS_PAIR_FAILED -> step = PairStep.Failed
                        }
                    }

                    override fun onTimeout() {
                        println("zcf onTimeout")
                        step = PairStep.Failed
                    }
                }
            )
        }

        //test
        MockMessageServiceImpl.getService().StartService(KeyPairFragment::class.java.toString())
    }

    private fun generateKeys() : List<Int> {
        return (1..8).map {
            Random.nextInt(10)
        }.toList()
    }

    private fun updateUI() {
        when (step) {
            PairStep.Home -> {
                binding.keyIcon.setImageResource(R.drawable.key_pair_green)
                binding.keyPairText.setText(R.string.key_pair_title)
                binding.keys.visibility = View.GONE
            }
            PairStep.Start -> {
                binding.keyIcon.setImageResource(R.drawable.key_pair_grey)
                binding.keyPairText.setText(R.string.key_pair_code)
                binding.keys.visibility = View.VISIBLE
                fillKeys()
            }
            PairStep.Pairing -> {
                binding.keyIcon.setImageResource(R.drawable.key_pair_in_progress)
                binding.keyPairText.setText(R.string.key_pair_in_progress)
                binding.keys.visibility = View.VISIBLE
                fillKeys()
            }
            PairStep.Success -> {
                binding.keyIcon.setImageResource(R.drawable.key_pair_success)
                binding.keyPairText.setText(R.string.key_pair_success)
                binding.keys.visibility = View.GONE
            }
            PairStep.Failed -> {
                binding.keyIcon.setImageResource(R.drawable.key_pair_grey)
                binding.keyPairText.setText(R.string.key_pair_failed)
                binding.keys.visibility = View.GONE
            }
        }
    }

    private fun fillKeys() {
        binding.key1.text = keys[0].toString()
        binding.key2.text = keys[1].toString()
        binding.key3.text = keys[2].toString()
        binding.key4.text = keys[3].toString()
        binding.key5.text = keys[4].toString()
        binding.key6.text = keys[5].toString()
        binding.key7.text = keys[6].toString()
        binding.key8.text = keys[7].toString()
    }

    companion object {
        const val TIMEOUT_MS = 30 * 1000
    }
}