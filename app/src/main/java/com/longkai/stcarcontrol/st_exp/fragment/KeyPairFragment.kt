package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
    private val TAG = KeyPairFragment::class.java.simpleName

    enum class PairStep {
        Home, Start, Pairing, Success, Failed;

        fun nextEligibleSteps(): List<PairStep> {
            return when(this) {
                Home -> listOf(Start)
                Start -> listOf(Pairing, Success, Failed)
                Pairing -> listOf(Success, Failed)
                Success -> listOf(Home)
                Failed -> listOf(Home)
            }
        }
    }

    private lateinit var binding: FragmentKeyPairBinding
    private val handler = Handler(Looper.getMainLooper())
    private var command: CMDKeyPairStart? = null

    private var step: PairStep = PairStep.Home
        set(value) {
            Log.i(TAG, "Step change, current: $field, next: $value")
            if (value in field.nextEligibleSteps()) {
                field = value
                Log.i(TAG, "Step changed to :$field")
                handler.post { updateUI() }
            }
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

        binding.keyPairIcon.setOnClickListener {
            if (step != PairStep.Home) return@setOnClickListener

            keys.clear()
            keys.addAll(generateKeys())
            step = PairStep.Start

            command = CMDKeyPairStart(keys)
            ServiceManager.getInstance().sendCommandToCar(command, CommandListenerAdapter<CMDKeyPairStart.Response>())
            ServiceManager.getInstance().registerRegularlyCommand(
                command,
                object : CommandListenerAdapter<CMDKeyPairStart.Response>(KEY_PAIR_TIMEOUT_MS) {
                    override fun onSuccess(response: CMDKeyPairStart.Response?) {
                        Log.i(TAG, "onSuccess, response status: ${response?.status}")
                        when (response?.status) {
                            CMDKeyPairStart.Response.STATUS_PAIR_IN_PROGRESS -> step = PairStep.Pairing
                            CMDKeyPairStart.Response.STATUS_PAIR_SUCCESS -> step = PairStep.Success
                            CMDKeyPairStart.Response.STATUS_PAIR_FAILED -> step = PairStep.Failed
                        }
                    }

                    override fun onTimeout() {
                        Log.i(TAG, "onTimeout")
                        step = PairStep.Failed
                    }
                }
            )


            handler.postDelayed(object: Runnable {
                override fun run() {
                    Log.i(TAG, "unregister command")
                    unregisterCommand()
                    step = PairStep.Failed
                }

            }, KEY_PAIR_TIMEOUT_MS.toLong())
        }

        binding.keyCheckIcon.setOnClickListener {
            Log.i(TAG, "childFragmentManager: $childFragmentManager")
            childFragmentManager.beginTransaction()
                .replace(R.id.keyCheckRootLayout, KeyCheckFragment())
                .commit()
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
                binding.keyPairText.setText(R.string.key_pair_title)
                binding.keyPairIcon.setImageResource(R.drawable.key_pair_green)
                binding.keyPairIcon.isEnabled = true
                binding.keys.visibility = View.GONE
                binding.keyCheckUIGroup.visibility = View.VISIBLE
            }
            PairStep.Start -> {
                binding.keyPairText.setText(R.string.key_pair_code)
                binding.keyPairIcon.setImageResource(R.drawable.key_pair_grey)
                binding.keyPairIcon.isEnabled = false
                binding.keys.visibility = View.VISIBLE
                binding.keyCheckUIGroup.visibility = View.GONE
                fillKeys()
            }
            PairStep.Pairing -> {
                binding.keyPairText.setText(R.string.key_pair_in_progress)
                binding.keyPairIcon.setImageResource(R.drawable.key_pair_in_progress)
                binding.keyPairIcon.isEnabled = false
                binding.keys.visibility = View.VISIBLE
                binding.keyCheckUIGroup.visibility = View.GONE
                fillKeys()
            }
            PairStep.Success -> {
                binding.keyPairText.setText(R.string.key_pair_success)
                binding.keyPairIcon.setImageResource(R.drawable.key_pair_success)
                binding.keyPairIcon.isEnabled = false
                binding.keys.visibility = View.GONE
                binding.keyCheckUIGroup.visibility = View.GONE
            }
            PairStep.Failed -> {
                binding.keyPairText.setText(R.string.key_pair_failed)
                binding.keyPairIcon.setImageResource(R.drawable.key_pair_failed)
                binding.keyPairIcon.isEnabled = false
                binding.keys.visibility = View.GONE
                binding.keyCheckUIGroup.visibility = View.GONE
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

    override fun onDestroy() {
        unregisterCommand()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun unregisterCommand() {
        if (command != null) {
            ServiceManager.getInstance().unregisterRegularlyCommand(command)
            command = null
        }
    }

    companion object {
        const val KEY_PAIR_TIMEOUT_MS = 60 * 1000
    }
}