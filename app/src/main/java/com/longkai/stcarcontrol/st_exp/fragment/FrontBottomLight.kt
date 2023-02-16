package com.longkai.stcarcontrol.st_exp.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.activity.MainActivity
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseCommand
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFrontC11LightList.*
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.databinding.FragmentFrontBottomLightBinding
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

/**
 * Created by Administrator on 2017/7/10.
 */
class FrontBottomLight : Fragment(), View.OnClickListener {
    private val TAG = "C11"

    private lateinit var binding: FragmentFrontBottomLightBinding
    private val rdoUrban: RadioButton by lazy { binding.rdoBtnHighBeamUrban }
    private val rdoHighway: RadioButton by lazy { binding.rdoBtnHighBeamHighway }
    private val rdoCountry: RadioButton by lazy { binding.rdoBtnHighBeamCountry }
    private val rdoCurve: RadioButton by lazy { binding.rdoBtnHighBeamCurve }
    private val rdoParking: RadioButton by lazy { binding.rdoBtnHighBeamParking }
    private val rdoEnergySaving: RadioButton by lazy { binding.rdoBtnHighBeamEnergySaving }
    private val rdoC11Pattern7: RadioButton by lazy { binding.rdoBtnC11Pattern7 }
    private val rdoBtnInvisible: RadioButton by lazy { binding.rdoBtnInvisible }
    private val gif_view_high_beam: GifImageView by lazy { binding.gifvHighBeam }

    private var highBeamStatus = 0
    private var lightMode: LightMode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFrontBottomLightBinding.inflate(inflater, container, false)
        rdoUrban.setOnClickListener(this)
        rdoHighway.setOnClickListener(this)
        rdoCountry.setOnClickListener(this)
        rdoCurve.setOnClickListener(this)
        rdoParking.setOnClickListener(this)
        rdoEnergySaving.setOnClickListener(this)
        rdoC11Pattern7.setOnClickListener(this)
        binding.ivHighBeamBack.setOnClickListener(this)

        setupSoundSwitchs()
        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rdoBtn_high_beam_urban ->                 //new command 的时候已经自动清零
                clickTask(rdoUrban, 1, R.mipmap.gif_high_beam_urban, CMDFrontC11Pattern1())
            R.id.rdoBtn_high_beam_highway -> clickTask(
                rdoHighway,
                2,
                R.mipmap.gif_high_beam_highway,
                CMDFrontC11Pattern2()
            )
            R.id.rdoBtn_high_beam_country -> clickTask(
                rdoCountry,
                3,
                R.mipmap.gif_high_beam_country,
                CMDFrontC11Pattern3()
            )
            R.id.rdoBtn_high_beam_curve -> clickTask(
                rdoCurve,
                4,
                R.mipmap.gif_high_beam_curve,
                CMDFrontC11Pattern4()
            )
            R.id.rdoBtn_high_beam_parking -> clickTask(
                rdoParking,
                5,
                R.mipmap.gif_high_beam_park,
                CMDFrontC11Pattern5()
            )
            R.id.rdoBtn_high_beam_energy_saving -> clickTask(
                rdoEnergySaving,
                6,
                R.mipmap.gif_high_beam_energy_saving,
                CMDFrontC11Pattern6()
            )
            R.id.rdoBtn_c11_pattern_7 -> clickTask(
                rdoC11Pattern7,
                7,
                R.mipmap.gif_high_beam_energy_saving,
                CMDFrontC11Pattern7()
            )
            R.id.iv_high_beam_back -> (activity as MainActivity?)!!.setSelect(1)
        }
    }

    private fun clickTask(rb: RadioButton?, num: Int, gifResId: Int, command: BaseCommand) {
//        return;
        if (highBeamStatus == num) {
            rdoBtnInvisible!!.isChecked = true
            highBeamStatus = 0
            releaseGifView()
            //send command
            command.turnOff()
            ServiceManager.getInstance()
                .sendCommandToCar(command, object : CommandListenerAdapter<BaseResponse>() {
                    override fun onSuccess(response: BaseResponse) {
                        super.onSuccess(response)
                        Log.i("C11", "onSuccess")
                    }

                    override fun onTimeout() {
                        super.onTimeout()
                        Log.i("C11", "onTimeout")
                    }
                })
        } else {
//            rb.setChecked(true);
            highBeamStatus = num
            loadGif(gifResId)
            command.turnOn()
            ServiceManager.getInstance()
                .sendCommandToCar(command, object : CommandListenerAdapter<BaseResponse>() {
                    override fun onSuccess(response: BaseResponse) {
                        super.onSuccess(response)
                        Log.i("C11", "onSuccess")
                    }

                    override fun onTimeout() {
                        super.onTimeout()
                        Log.i("C11", "onTimeout")
                    }
                })
        }
    }

    private fun loadGif(resID: Int) {
        try {
            // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
            // 将gif图资源转化为GifDrawable
            val gifDrawable = GifDrawable(resources, resID)

            // gif1加载一个动态图gif
            gif_view_high_beam!!.setImageDrawable(gifDrawable)

            // 如果是普通的图片资源，就像Android的ImageView set图片资源一样简单设置进去即可。
            // gif2加载一个普通的图片（如png，bmp，jpeg等等）
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseGifView() {
        try {
            gif_view_high_beam!!.setImageDrawable(null)
            gif_view_high_beam!!.setImageResource(R.mipmap.ic_highbeam_car)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupSoundSwitchs() {
        val lightModeCUnlockAnimator = ValueAnimator.ofInt(100, 0).setDuration(3000)
        binding.lightModeCLock.setOnClickListener {
            if (binding.lightModeCUnlockProgress.progress != 100) return@setOnClickListener
            lightModeCUnlockAnimator.addUpdateListener {
                binding.lightModeCUnlockProgress.progress = it.animatedValue as Int
                if (it.animatedValue == 0) {
                    binding.lightModeCLock.isVisible = false
                    binding.lightModeCUnlockProgress.isVisible = false
                    lightModeCUnlocked = true
                }
            }
            lightModeCUnlockAnimator.start()
        }
        val lightModeDUnlockAnimator = ValueAnimator.ofInt(100, 0).setDuration(3000)
        binding.lightModeDLock.setOnClickListener {
            if (binding.lightModeDUnlockProgress.progress != 100) return@setOnClickListener
            lightModeDUnlockAnimator.addUpdateListener {
                binding.lightModeDUnlockProgress.progress = it.animatedValue as Int
                if (it.animatedValue == 0) {
                    binding.lightModeDLock.isVisible = false
                    binding.lightModeDUnlockProgress.isVisible = false
                    lightModeDUnlocked = true
                }
            }
            lightModeDUnlockAnimator.start()
        }

        binding.lightModesResetBtn.setOnClickListener {
            lightModeCUnlockAnimator.cancel()
            binding.lightModeCLock.isVisible = true
            binding.lightModeCUnlockProgress.isVisible = true
            binding.lightModeCUnlockProgress.progress = 100
            lightModeDUnlockAnimator.cancel()
            binding.lightModeDLock.isVisible = true
            binding.lightModeDUnlockProgress.isVisible = true
            binding.lightModeDUnlockProgress.progress = 100
        }

        if (lightModeCUnlocked) {
            binding.lightModeCLock.isVisible = false
            binding.lightModeCUnlockProgress.isVisible = false
        } else {
            binding.lightModeCLock.isVisible = true
            binding.lightModeCUnlockProgress.isVisible = true
            binding.lightModeCUnlockProgress.progress = 100
        }

        if (lightModeDUnlocked) {
            binding.lightModeDLock.isVisible = false
            binding.lightModeDUnlockProgress.isVisible = false
        } else {
            binding.lightModeDLock.isVisible = true
            binding.lightModeDUnlockProgress.isVisible = true
            binding.lightModeDUnlockProgress.progress = 100
        }

        binding.lightModeA.setOnClickListener {
            setLightMode(LightMode.CHRISTMAS)
        }
        binding.lightModeB.setOnClickListener {
            setLightMode(LightMode.FIREWORK)
        }
        binding.lightModeC.setOnClickListener {
            if (lightModeCUnlocked) setLightMode(LightMode.ROSE)
        }
        binding.lightModeD.setOnClickListener {
            if (lightModeDUnlocked) setLightMode(LightMode.MID_AUTUMN)
        }
    }

    private fun setLightMode(lightMode: LightMode) {
        sendLightCommand(lightMode)
        setLightSwitchState()
    }

    private fun setLightSwitchState() {
        val selectedColor = requireContext().getColor(R.color.colorAccent)
        val deselectedColor = requireContext().getColor(R.color.colorBlack)
        binding.lightModeA.setColorFilter(if (lightMode == LightMode.CHRISTMAS) selectedColor else deselectedColor)
        binding.lightModeB.setColorFilter(if (lightMode == LightMode.FIREWORK) selectedColor else deselectedColor)
        binding.lightModeC.setColorFilter(if (lightMode == LightMode.ROSE) selectedColor else deselectedColor)
        binding.lightModeD.setColorFilter(if (lightMode == LightMode.MID_AUTUMN) selectedColor else deselectedColor)
    }

    private fun sendLightCommand(lightMode: LightMode) {
        if (this.lightMode != lightMode) {
            Log.i(TAG, "Turn on light mode: $lightMode")
            this.lightMode = lightMode
            val command = lightMode.command()
            command.turnOn()
            ServiceManager.getInstance()
                .sendCommandToCar(command, object : CommandListenerAdapter<BaseResponse>() {
                    override fun onSuccess(response: BaseResponse) {
                        super.onSuccess(response)
                        Log.i(TAG, "onSuccess")
                    }

                    override fun onTimeout() {
                        super.onTimeout()
                        Log.i(TAG, "onTimeout")
                    }
                })
        } else {
            Log.i(TAG, "Turn off light mode: $lightMode")
            this.lightMode = null
            val command = lightMode.command()
            command.turnOff()
            ServiceManager.getInstance()
                .sendCommandToCar(command, object : CommandListenerAdapter<BaseResponse>() {
                    override fun onSuccess(response: BaseResponse) {
                        super.onSuccess(response)
                        Log.i(TAG, "onSuccess")
                    }

                    override fun onTimeout() {
                        super.onTimeout()
                        Log.i(TAG, "onTimeout")
                    }
                })
        }
    }

    private fun LightMode.command() = when (this) {
        LightMode.CHRISTMAS -> CMDFrontC11Mode1()
        LightMode.FIREWORK -> CMDFrontC11Mode2()
        LightMode.ROSE -> CMDFrontC11Mode3()
        LightMode.MID_AUTUMN -> CMDFrontC11Mode4()
    }

    private enum class LightMode {
        CHRISTMAS, FIREWORK, ROSE, MID_AUTUMN
    }
}

private var lightModeCUnlocked = false
private var lightModeDUnlocked = false