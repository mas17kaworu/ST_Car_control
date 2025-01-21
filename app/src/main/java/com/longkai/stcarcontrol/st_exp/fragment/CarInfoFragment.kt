package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.Utils.clearViewRefreshRunnable
import com.longkai.stcarcontrol.st_exp.Utils.pauseRunnable
import com.longkai.stcarcontrol.st_exp.Utils.recycleHandler
import com.longkai.stcarcontrol.st_exp.Utils.resumeRunnable
import com.longkai.stcarcontrol.st_exp.activity.MainActivity
import com.longkai.stcarcontrol.st_exp.activity.VCUActivity
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDDFA
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDVCU.CMDDFAResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl
import com.longkai.stcarcontrol.st_exp.view.AppProgressViewBean
import com.longkai.stcarcontrol.st_exp.view.AppsInfoLayout
import com.longkai.stcarcontrol.st_exp.view.IndicatorColor
import com.longkai.stcarcontrol.st_exp.view.IndicatorView
import com.longkai.stcarcontrol.st_exp.view.isVersionUpgrade
var ac_Charge_Value = false
class CarInfoFragment : Fragment() {
    private var TAG ="CarInfoFragment"
    private var mRootView: View? = null
    private var mList: List<AppProgressViewBean> = ArrayList()
    private var mListView: ArrayList<View> = ArrayList()
    private var selectIndex = 0;
    private var appsInfoLayout: AppsInfoLayout? = null
    private var viewClick: OnClickListener = OnClickListener {
        it.tag?.apply {
            selectIndex = this as Int
            appsInfoLayout?.updateSelectByIndex(selectIndex)
            refreshViewSelect()
        }

    }
    private var postMsg: Runnable = Runnable {
        ServiceManager.getInstance()
            .sendCommandToCar(CMDDFA(), object : CommandListenerAdapter<CMDDFAResponse>() {
                override fun onSuccess(response: CMDDFAResponse?) {
                    super.onSuccess(response)
                    response?.let {
                        recycleHandler.post { refreshView(response) }
                    }
                }
                override fun onError(errorCode: Int) {
                    super.onError(errorCode)
                }

                override fun onTimeout() {
                    super.onTimeout()
                }
            })
        sendMsg()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mList = initAppInfo()
        mRootView = inflater.inflate(R.layout.fragment_carinfo_layout, null)
        appsInfoLayout = mRootView?.findViewById<AppsInfoLayout>(R.id.apps_info)?.apply {
            initData(mList)
            setClickListener(object : AppsInfoLayout.AppInfoClick {
                override fun viewClick(index: Any) {
                    selectIndex = index as Int
                    refreshViewSelect()
                }
            })
        }
        addViews()
        // Test
        MockMessageServiceImpl.getService().StartService(CarInfoFragment::class.java.toString())
        sendMsg()

        return mRootView
    }

    override fun onResume() {
        super.onResume()
        changeWifiViewVisible(false)
        resumeRunnable()
    }

    override fun onPause() {
        super.onPause()
        changeWifiViewVisible(true)
        pauseRunnable()
    }


    private fun changeWifiViewVisible(visible: Boolean){
        activity?.takeIf { activity is VCUActivity } ?.let {
            (activity as VCUActivity) ?.apply {
                changeWifiConnectVisible(visible)
            }
        }
    }

    private fun initAppInfo(): List<AppProgressViewBean> {
        var list = ArrayList<AppProgressViewBean>()
        list.add(AppProgressViewBean(text = "APP #1", percent = 0.15f, des = "VCU", maxRand = 10))
        list.add(
            AppProgressViewBean(
                "APP #2",
                percent = 0.25f,
                des = "Traction",
                maxRand = 10,
                needCheckAccAndResolver = true
            )
        )
        list.add(
            AppProgressViewBean(
                "APP #3",
                percent = 0.50f,
                des = "BMS",
                maxRand = 20,
                needCheckDFA = true
            )
        )
        list.add(
            AppProgressViewBean(
                "APP #4",
                percent = OBCPercentage,
                des = "OBC",
                overlap = false,
                maxRand = OBCRange
            )
        )
        list.add(
            AppProgressViewBean(
                "APP #5",
                percent = 0.07f,
                des = "DC/DC",
                overlap = false,
                maxRand = 6
            )
        )
        list.add(
            AppProgressViewBean(
                "APP #6",
                percent = 0.16f,
                des = "AutoSAR & FuSa",
                maxRand = 8
            )
        )
        return list;
    }

    private fun addViews() {
        mListView.clear()
        var index = 0;
        mRootView?.let {
            var view1 = it.findViewById<View>(R.id.car_info_layout_1)
            view1.tag = index++
            view1.isSelected = true
            view1.setOnClickListener(viewClick)
            mListView.add(view1)

            var view2 = it.findViewById<View>(R.id.car_info_layout_2)
            view2.tag = index++
            view2.setOnClickListener(viewClick)
            mListView.add(view2)

            var view3 = it.findViewById<View>(R.id.car_info_layout_3)
            view3.tag = index++
            view3.setOnClickListener(viewClick)
            mListView.add(view3)

            var view4 = it.findViewById<View>(R.id.car_info_layout_4)
            view4.tag = index++
            view4.setOnClickListener(viewClick)
            mListView.add(view4)

            var view5 = it.findViewById<View>(R.id.car_info_layout_5)
            view5.tag = index++
            view5.setOnClickListener(viewClick)
            mListView.add(view5)

            var view6 = it.findViewById<View>(R.id.car_info_layout_6)
            view6.tag = index++
            view6.setOnClickListener(viewClick)
            mListView.add(view6)
        }
    }

    private fun refreshViewSelect() {
        var index = 0
        for (view in mListView) {
            view.isSelected = selectIndex == index++
        }
    }

    private fun sendMsg() {
        mRootView?.removeCallbacks(postMsg)
        mRootView?.postDelayed(postMsg, 1000)
        Log.i(TAG,"sendMsg")
    }

    private fun refreshView(response: CMDDFAResponse) {
        ac_Charge_Value = response.ac_c
        if (ac_Charge_Value) {
            OBCPercentage = 0.35f
            OBCRange = 10
        } else {
            OBCPercentage = 0.04f
            OBCRange = 1
        }


        if (response.crash) {
            mRootView?.findViewById<Switch>(R.id.vcu_acc)?.isChecked = false
            mRootView?.findViewById<Switch>(R.id.vcu_switch)?.isChecked = false
            mRootView?.findViewById<Switch>(R.id.vcu_generator)?.isChecked = false
            mRootView?.findViewById<IndicatorView>(R.id.vcu_pedal)?.changeCircleColor(IndicatorColor.COLOR_GRAY)
            mRootView?.findViewById<IndicatorView>(R.id.vcu_brake)?.changeCircleColor(IndicatorColor.COLOR_GRAY)
        }
        mRootView?.findViewById<IndicatorView>(R.id.carinfo_crash)?.let {
            it.changeCircleColor(if (response.crash) IndicatorColor.COLOR_GREEN else IndicatorColor.COLOR_GRAY)
        }
        mRootView?.findViewById<IndicatorView>(R.id.carinfo_dc_c)?.let {
            it.changeCircleColor(if (response.dc_c) IndicatorColor.COLOR_GREEN else IndicatorColor.COLOR_GRAY)
        }
        mRootView?.findViewById<IndicatorView>(R.id.carinfo_ac_charge)
            ?.changeCircleColor(if (response.ac_c) IndicatorColor.COLOR_GREEN else IndicatorColor.COLOR_GRAY)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearViewRefreshRunnable()
    }

    companion object {
        var OBCPercentage: Float = 0.03f
        var OBCRange: Int = 3
    }
}
