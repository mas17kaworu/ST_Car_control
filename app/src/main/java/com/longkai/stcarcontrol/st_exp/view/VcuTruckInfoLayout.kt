package com.longkai.stcarcontrol.st_exp.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RelativeLayout
import android.widget.Switch
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDSEND
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDSTATUS
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter

class VcuTruckInfoLayout:RelativeLayout {
    private var mPower:Switch? = null
    private final var TAG = "VcuTruckInfoLayout"
    //重试此时&重试次数
    private var retryCounts = 3;
    private var retryDelayTime = 3000L
    private var sendCMDSEND :CMDSEND?= null
    var retryRunnable :Runnable = Runnable{
        sendCMDSEND?.let {
            sendMsg(it)
        }
    }
    constructor(context: Context):super(context){
        initLayout()
    }
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet){
        initLayout()
    }


    private fun initLayout(){
        LayoutInflater.from(context).inflate(R.layout.vcu_truck_layout,this)
        mPower = findViewById(R.id.vcu_switch)
        mPower?.let {
            it.setOnCheckedChangeListener(object :OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    CMDSTATUS.sPower = isChecked
                    var comment = CMDSEND(isChecked, CMDSTATUS.sMotor)
                    sendMsg(comment)
                }
            })
        }
    }

    /**
     * 失败重试三次
     */
    private fun sendMsg(cmdsend: CMDSEND) {
        sendCMDSEND = cmdsend
        ServiceManager.getInstance()
            .sendCommandToCar(cmdsend, object : CommandListenerAdapter<CMDResponse>() {
                override fun onSuccess(response: CMDResponse?) {
                    super.onSuccess(response)
                    retryCounts = 0;
                    removeCallbacks(retryRunnable)
                }

                override fun onError(errorCode: Int) {
                    super.onError(errorCode)
                    Log.d(TAG,"onErr")
                }

                override fun onTimeout() {
                    super.onTimeout()
                    Log.d(TAG,"timeout")
                }

                override fun getTimeout(): Int {
                    return 60000
                }
            })
        if (--retryCounts > 0) {
            postDelayed({ sendMsg(cmdsend) }, retryDelayTime)
            Log.d(TAG, "sendMsg retryCounts${retryCounts}")
        }
    }



}