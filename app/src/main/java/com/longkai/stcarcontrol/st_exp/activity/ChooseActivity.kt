package com.longkai.stcarcontrol.st_exp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.longkai.stcarcontrol.st_exp.ConstantData
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.STCarApplication.Companion.logConfig
import com.longkai.stcarcontrol.st_exp.STCarApplication.Companion.verifyStoragePermissions
import com.longkai.stcarcontrol.st_exp.Utils.SharedPreferencesUtil
import com.longkai.stcarcontrol.st_exp.communication.ConnectionListener
import com.longkai.stcarcontrol.st_exp.communication.ConnectionType
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDGetVersion
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.compose.DdsActivity
import com.longkai.stcarcontrol.st_exp.pbox.TrackingActivity
import java.util.*

/**
 *
 * Created by Administrator on 2018/5/12.
 */
class ChooseActivity : BaseActivity(), View.OnClickListener {
    private var ivBTConnectionState: ImageView? = null
    private var ivWifiConnectionState: ImageView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_first)
        verifyStoragePermissions(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> for (permission in permissions) {
                if (permission.equals(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        ignoreCase = true
                    )
                ) {
                    logConfig()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initUI()
        var verName: String? = "version"
        try {
            verName = applicationContext.packageManager.getPackageInfo(
                applicationContext.packageName,
                0
            ).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        (findViewById<View>(R.id.tv_choose_activity_version) as TextView).text = verName
        ServiceManager.getInstance().init(applicationContext) {
            ServiceManager.getInstance().setConnectionListener(mConnectionListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ChooseActivity", "onDestroy: ChooseActivity")
        ServiceManager.getInstance().destroy()
    }

    private fun initUI() {
        findViewById<View>(R.id.btn_choose_function).setOnClickListener(this)
        findViewById<View>(R.id.btn_choose_VCU).setOnClickListener(this)
        findViewById<View>(R.id.btn_choose_entertainment).setOnClickListener(this)
        findViewById<View>(R.id.btn_choose_dds).setOnClickListener(this)
        findViewById<View>(R.id.btn_choose_tracking).setOnClickListener(this)
        ivBTConnectionState = findViewById<View>(R.id.iv_chooseactivity_lost_bluetooth) as ImageView
        ivBTConnectionState!!.setOnClickListener(this)
        ivWifiConnectionState = findViewById<View>(R.id.iv_chooseactivity_lost_wifi) as ImageView
        ivWifiConnectionState!!.setOnClickListener(this)
        if (communicationEstablished) {
            ivWifiConnectionState!!.visibility = View.INVISIBLE
            ivBTConnectionState!!.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_choose_function -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_choose_VCU -> {
                val intent2 = Intent(this, VCUActivity::class.java)
                startActivity(intent2)
            }
            R.id.btn_choose_entertainment -> {
                val intent3 = Intent(this, InfotainmentActivity::class.java)
                startActivity(intent3)
            }
            R.id.btn_choose_dds -> startActivity(Intent(this, DdsActivity::class.java))
            R.id.btn_choose_tracking -> startActivity(Intent(this, TrackingActivity::class.java))
            R.id.iv_chooseactivity_lost_wifi -> {
                SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "WIFI")
                if (!hardwareConnected) {
                    ServiceManager.getInstance()
                        .connectToDevice(null, mConnectionListener, ConnectionType.Wifi)
                }
                ServiceManager.getInstance().sendCommandToCar(CMDGetVersion(), getVersionListener)
            }
            R.id.iv_chooseactivity_lost_bluetooth -> {
                SharedPreferencesUtil.put(this, ConstantData.CONNECTION_TYPE, "BT")
                if (!hardwareConnected) {
                    ServiceManager.getInstance()
                        .connectToDevice(null, mConnectionListener, ConnectionType.BT)
                }
                ServiceManager.getInstance().sendCommandToCar(CMDGetVersion(), getVersionListener)
            }
        }
    }

    var timer: Timer? = null
    var mConnectionListener: ConnectionListener = object : ConnectionListener {
        override fun onConnected() {
//            Toast.makeText(getApplicationContext(), "Bt Connected", Toast.LENGTH_SHORT).show();
            hardwareConnected = true
            if (null == timer) {
                timer = Timer()
            }
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    ServiceManager.getInstance()
                        .sendCommandToCar(CMDGetVersion(), getVersionListener)
                }
            }, 2000) //等两秒发送get version command
        }

        override fun onDisconnected() {
            Toast.makeText(applicationContext, "Disconnected", Toast.LENGTH_LONG).show()
            hardwareConnected = false
            communicationEstablished = false
            ivWifiConnectionState!!.visibility = View.VISIBLE
            ivBTConnectionState!!.visibility = View.VISIBLE
        }
    }
    var mVersion: String? = null
    private val getVersionListener: CommandListenerAdapter<*> =
        object : CommandListenerAdapter<BaseResponse>() {
            override fun onSuccess(response: BaseResponse) {
                super.onSuccess(response)
                //invisible View
                mVersion = (response as CMDGetVersion.Response).version
                communicationEstablished = true
                runOnUiThread {
                    ivBTConnectionState!!.visibility = View.INVISIBLE
                    ivWifiConnectionState!!.visibility = View.INVISIBLE
                    Toast.makeText(
                        applicationContext,
                        "version:$mVersion", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onTimeout() {
                super.onTimeout()
            }
        }
}