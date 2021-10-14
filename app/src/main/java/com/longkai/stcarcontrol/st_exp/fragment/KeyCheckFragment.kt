package com.longkai.stcarcontrol.st_exp.fragment

import android.content.Context
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
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDKeyPairList.CMDKeyCheck
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.databinding.FragmentKeyCheckBinding
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl

class KeyCheckFragment : Fragment() {
    private val TAG = KeyCheckFragment::class.java.simpleName

    private lateinit var binding: FragmentKeyCheckBinding
    private val handler = Handler(Looper.getMainLooper())
    private var keyFound: Boolean = false
        private set(value) {
            field = value
            Log.i(TAG, "KeyFound changed to :$field")
            handler.post { updateUI() }
        }

    private var command: CMDKeyCheck? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKeyCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.beginTransaction()
                    .remove(this@KeyCheckFragment)
                    .commit()
            }
        })

        //test
        MockMessageServiceImpl.getService().StopService(KeyPairFragment::class.java.toString())
        MockMessageServiceImpl.getService().StartService(KeyCheckFragment::class.java.toString())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        command = CMDKeyCheck()
        ServiceManager.getInstance().registerRegularlyCommand(
            command,
            object : CommandListenerAdapter<CMDKeyCheck.Response>() {
                override fun onSuccess(response: CMDKeyCheck.Response?) {
                    Log.i(TAG, "onSuccess, response status: ${response?.status}")
                    when(response?.status) {
                        CMDKeyCheck.Response.STATUS_FOUND -> keyFound = true
                        CMDKeyCheck.Response.STATUS_NOT_FOUND -> keyFound = false
                    }
                }
            }
        )
    }

    override fun onStop() {
        unregisterCommand()
        super.onStop()
    }

    private fun unregisterCommand() {
        if (command != null) {
            ServiceManager.getInstance().unregisterRegularlyCommand(command)
            command = null
        }
    }

    fun updateUI() {
        val keyCheckResultResId = if (keyFound) R.string.key_check_found else R.string.key_check_not_found
        binding.keyCheckResult.setText(keyCheckResultResId)
    }
}