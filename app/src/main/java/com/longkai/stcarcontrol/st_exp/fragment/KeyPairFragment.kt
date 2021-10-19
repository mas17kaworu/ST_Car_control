package com.longkai.stcarcontrol.st_exp.fragment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.CountDownTimer
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
                Start -> listOf(Pairing, Success, Failed, Home)
                Pairing -> listOf(Success, Failed, Home)
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
                cancelKeyPair()
                step = PairStep.Home
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
                        cancelKeyPair()
                        step = PairStep.Failed
                    }
                }
            )

            //test
            MockMessageServiceImpl.getService().StopService(KeyCheckFragment::class.java.toString())
            MockMessageServiceImpl.getService().StartService(KeyPairFragment::class.java.toString())
        }

        binding.keyCheckIcon.setOnClickListener {
            Log.i(TAG, "childFragmentManager: $childFragmentManager")
            childFragmentManager.beginTransaction()
                .replace(R.id.keyCheckRootLayout, KeyCheckFragment())
                .commit()
        }
    }

    private fun generateKeys() : List<Int> {
        return (1..8).map {
            Random.nextInt(10)
        }.toList()
    }

    private fun updateUI() {
        when (step) {
            PairStep.Home -> {
                binding.keyPairText.setText(R.string.key_page_title)
                binding.keyPairIcon.setImageResource(R.drawable.key_pair_green)
                binding.keyPairIcon.isEnabled = true
                binding.keys.visibility = View.GONE

            }
            PairStep.Start -> {
                binding.keyPairText.setText(R.string.key_pair_code)
                binding.keyPairIcon.setImageResource(R.drawable.key_pair_grey)
                binding.keyPairIcon.isEnabled = false
                binding.keys.visibility = View.VISIBLE
                fillKeys()
            }
            PairStep.Pairing -> {
                binding.keyPairText.setText(R.string.key_pair_in_progress)
                binding.keyPairIcon.setImageResource(R.drawable.key_pair_in_progress)
                binding.keyPairIcon.isEnabled = false
                binding.keys.visibility = View.VISIBLE
                fillKeys()
            }
            PairStep.Success -> {
                binding.keyPairText.setText(R.string.key_pair_success)
                binding.keyPairIcon.setImageResource(R.drawable.key_pair_success)
                binding.keyPairIcon.isEnabled = false
                binding.keys.visibility = View.GONE
            }
            PairStep.Failed -> {
                binding.keyPairText.setText(R.string.key_pair_failed)
                binding.keyPairIcon.setImageResource(R.drawable.key_pair_failed)
                binding.keyPairIcon.isEnabled = false
                binding.keys.visibility = View.GONE
            }
        }

        binding.keyCheckUIGroup.visibility = if (step == PairStep.Home) View.VISIBLE else View.GONE

        if (step == PairStep.Start) {
            binding.timer.visibility = View.VISIBLE
            keyPairStartTimer.start()
        } else {
            binding.timer.visibility = View.GONE
            keyPairStartTimer.cancel()
        }

        if (step == PairStep.Pairing) {
            scheduleTimeOutCancel()
            pairingAnimator.start()
        } else {
            removeTimeOutCancel()
            pairingAnimator.end()
        }
    }

    private val pairingAnimator by lazy {
        val animator = ObjectAnimator.ofFloat(binding.keyPairIcon, "rotation", -360f)
        animator.duration = 1000
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = ValueAnimator.INFINITE
        animator
    }

    private val keyPairStartTimer = object : CountDownTimer(KEY_PAIR_START_TIMEOUT_MS, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            binding.timer.setText(getString(R.string.key_pair_start_timeout_msg, millisUntilFinished/1000))
        }

        override fun onFinish() {
            cancelKeyPair()
            step = PairStep.Failed
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
        cancelKeyPair()
        keyPairStartTimer.cancel()
        pairingAnimator.cancel()
        super.onDestroy()
    }

    private fun cancelKeyPair() {
        if (command != null) {
            ServiceManager.getInstance().unregisterRegularlyCommand(command)
            command = null
        }
        ServiceManager.getInstance().sendCommandToCar(
            CMDKeyPairCancel(),
            CommandListenerAdapter<CMDKeyPairCancel.Response>()
        )
        handler.removeCallbacksAndMessages(null)
    }

    val timeOutCancelRunnable = object: Runnable {
        override fun run() {
            Log.i(TAG, "timeout cancel")
            cancelKeyPair()
            step = PairStep.Failed
        }
    }

    private fun scheduleTimeOutCancel() {
        handler.removeCallbacks(timeOutCancelRunnable)
        handler.postDelayed(timeOutCancelRunnable, KEY_PAIR_TIMEOUT_MS.toLong())
    }

    private fun removeTimeOutCancel() {
        handler.removeCallbacks(timeOutCancelRunnable)
    }

    companion object {
        const val KEY_PAIR_START_TIMEOUT_MS = 60 * 1000L
        const val KEY_PAIR_TIMEOUT_MS = 30 * 1000
    }
}