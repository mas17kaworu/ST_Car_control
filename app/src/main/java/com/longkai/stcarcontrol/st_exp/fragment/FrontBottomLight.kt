package com.longkai.stcarcontrol.st_exp.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.activity.MainActivity
import com.longkai.stcarcontrol.st_exp.communication.ServiceManager
import com.longkai.stcarcontrol.st_exp.communication.commandList.BaseResponse
import com.longkai.stcarcontrol.st_exp.communication.commandList.CMDFrontC11LightList.*
import com.longkai.stcarcontrol.st_exp.communication.commandList.CommandListenerAdapter
import com.longkai.stcarcontrol.st_exp.databinding.FragmentFrontBottomLightBinding
import com.longkai.stcarcontrol.st_exp.music.STPlayer
import pl.droidsonroids.gif.GifDrawable

/**
 * Created by Administrator on 2017/7/10.
 */
class FrontBottomLight : Fragment() {
    private val TAG = "C11"

    private lateinit var binding: FragmentFrontBottomLightBinding

    private var lightPattern: LightPattern? = null
    private var lightMode: LightMode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFrontBottomLightBinding.inflate(inflater, container, false)

        setupLightPatterns()
        setupLightModes()
        stPlayer = STPlayer(requireContext())
        return binding.root
    }

    private fun setupLightPatterns() {
        binding.apply {
            rdoBtnHighBeamUrban.setOnClickListener { setLightPattern(LightPattern.Pattern1) }
            rdoBtnHighBeamHighway.setOnClickListener { setLightPattern(LightPattern.Pattern2) }
            rdoBtnHighBeamCountry.setOnClickListener { setLightPattern(LightPattern.Pattern3) }
            rdoBtnHighBeamCurve.setOnClickListener { setLightPattern(LightPattern.Pattern4) }
            rdoBtnHighBeamParking.setOnClickListener { setLightPattern(LightPattern.Pattern5) }
            rdoBtnHighBeamEnergySaving.setOnClickListener { setLightPattern(LightPattern.Pattern6) }
            rdoBtnC11Pattern7.setOnClickListener { setLightPattern(LightPattern.Pattern7) }
            ivHighBeamBack.setOnClickListener { (activity as MainActivity?)!!.setSelect(1) }
            btnPlayAudio.setOnClickListener{ playOrPause() }
            btnPlayPrevious.setOnClickListener{ stPlayer.previous() }
            btnPlayNext.setOnClickListener { stPlayer.next() }
        }
    }

    private enum class LightPattern {
        Pattern1, Pattern2, Pattern3, Pattern4, Pattern5, Pattern6, Pattern7
    }

    private fun LightPattern.gifResId() = when (this) {
        LightPattern.Pattern1 -> R.mipmap.gif_high_beam_urban
        LightPattern.Pattern2 -> R.mipmap.gif_high_beam_highway
        LightPattern.Pattern3 -> R.mipmap.gif_high_beam_country
        LightPattern.Pattern4 -> R.mipmap.gif_high_beam_curve
        LightPattern.Pattern5 -> R.mipmap.gif_high_beam_park
        LightPattern.Pattern6 -> R.mipmap.gif_high_beam_energy_saving
        LightPattern.Pattern7 -> R.mipmap.gif_high_beam_energy_saving
    }

    private fun LightPattern.command() = when (this) {
        LightPattern.Pattern1 -> CMDFrontC11Pattern1()
        LightPattern.Pattern2 -> CMDFrontC11Pattern2()
        LightPattern.Pattern3 -> CMDFrontC11Pattern3()
        LightPattern.Pattern4 -> CMDFrontC11Pattern4()
        LightPattern.Pattern5 -> CMDFrontC11Pattern5()
        LightPattern.Pattern6 -> CMDFrontC11Pattern6()
        LightPattern.Pattern7 -> CMDFrontC11Pattern7()
    }

    private fun setLightPattern(lightPattern: LightPattern?) {
        if (lightMode != null) {
            setLightMode(null)
        }

        val command: CMDFrontC11Light?
        if (this.lightPattern != lightPattern && lightPattern != null) {
            command = lightPattern.command()
            command.turnOn()
            this.lightPattern = lightPattern

        } else {
            command = this.lightPattern?.command()
            command?.turnOff()
            this.lightPattern = null
        }

        setLightPatternState(this.lightPattern)

        if (command != null) {
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

    private fun setLightPatternState(lightPattern: LightPattern?) {
        if (lightPattern != null) {
            loadGif(lightPattern.gifResId())
        } else {
            releaseGifView()
            binding.rdoBtnInvisible.isChecked = true
        }
    }

    private fun loadGif(resID: Int) {
        try {
            // 如果加载的是gif动图，第一步需要先将gif动图资源转化为GifDrawable
            // 将gif图资源转化为GifDrawable
            val gifDrawable = GifDrawable(resources, resID)

            // gif1加载一个动态图gif
            binding.gifvHighBeam.setImageDrawable(gifDrawable)

            // 如果是普通的图片资源，就像Android的ImageView set图片资源一样简单设置进去即可。
            // gif2加载一个普通的图片（如png，bmp，jpeg等等）
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseGifView() {
        try {
            binding.gifvHighBeam.apply {
                setImageDrawable(null)
                setImageResource(R.mipmap.ic_highbeam_car)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupLightModes() {
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

    private fun setLightMode(lightMode: LightMode?) {
        if (this.lightPattern != null) {
            setLightPattern(null)
        }

        val command: CMDFrontC11Light?
        if (this.lightMode != lightMode && lightMode != null) {
            command = lightMode.command()
            command.turnOn()
            this.lightMode = lightMode
        } else {
            command = this.lightMode?.command()
            command?.turnOff()
            this.lightMode = null
        }

        setLightModeState(this.lightMode)

        if (command != null) {
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

    private fun setLightModeState(lightMode: LightMode?) {
        val selectedColor = requireContext().getColor(R.color.colorAccent)
        val deselectedColor = requireContext().getColor(R.color.colorBlack)
        binding.lightModeA.setColorFilter(if (lightMode == LightMode.CHRISTMAS) selectedColor else deselectedColor)
        binding.lightModeB.setColorFilter(if (lightMode == LightMode.FIREWORK) selectedColor else deselectedColor)
        binding.lightModeC.setColorFilter(if (lightMode == LightMode.ROSE) selectedColor else deselectedColor)
        binding.lightModeD.setColorFilter(if (lightMode == LightMode.MID_AUTUMN) selectedColor else deselectedColor)
    }

    private enum class LightMode {
        CHRISTMAS, FIREWORK, ROSE, MID_AUTUMN
    }

    private fun LightMode.command() = when (this) {
        LightMode.CHRISTMAS -> CMDFrontC11Mode1()
        LightMode.FIREWORK -> CMDFrontC11Mode2()
        LightMode.ROSE -> CMDFrontC11Mode3()
        LightMode.MID_AUTUMN -> CMDFrontC11Mode4()
    }


    private lateinit var stPlayer: STPlayer
    private fun playOrPause() {
        if (stPlayer.isPlaying()) {
            stPlayer.stop()
            binding.btnPlayAudio.setImageResource(R.mipmap.ic_play)
        } else {
            stPlayer.playMusic()
            binding.btnPlayAudio.setImageResource(R.mipmap.ic_stop)
        }
    }
}

private var lightModeCUnlocked = false
private var lightModeDUnlocked = false