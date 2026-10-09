package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.testTag
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.ChassisRoute
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.ChassisViewModel
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme
import com.longkai.stcarcontrol.st_exp.mockMessage.MockMessageServiceImpl

class VCUChassisFragment : Fragment() {
    private var chassisViewModel: ChassisViewModel? = null
    private var navigationLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        id = R.id.chassis_compose_view
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appContainer = (requireActivity().application as STCarApplication).appContainer
        val viewModel = ViewModelProvider(
            this,
            ChassisViewModel.provideFactory(appContainer.chassisRepository)
        )[ChassisViewModel::class.java]
        chassisViewModel = viewModel
        viewModel.onPageEntered()
        val navigation = requireActivity().findViewById<View>(R.id.vcu_horizon_listview)
        val navigationInsetPx = mutableStateOf(resources.getDimensionPixelSize(R.dimen.chassis_vcu_navigation_inset))
        val contentLocation = IntArray(2)
        val navigationLocation = IntArray(2)
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            if (view.height > 0 && navigation.height > 0) {
                view.getLocationInWindow(contentLocation)
                navigation.getLocationInWindow(navigationLocation)
                // Both positions already include the Activity's system-inset handling.
                navigationInsetPx.value =
                    (contentLocation[1] + view.height - navigationLocation[1]).coerceIn(0, view.height)
            }
        }
        navigationLayoutListener = listener
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        (view as ComposeView).setContent {
            STCarTheme {
                ProvideWindowInsets {
                    val navigationInset = with(LocalDensity.current) { navigationInsetPx.value.toDp() }
                    Box(Modifier.fillMaxSize()) {
                        Box(
                            Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                                .height(navigationInset).background(Color(0xFF1B212B))
                        )
                        ChassisRoute(
                            viewModel = viewModel,
                            modifier = Modifier.statusBarsPadding().padding(bottom = navigationInset)
                                .testTag("chassis-content")
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (STCarApplication.inUIDebugMode) {
            MockMessageServiceImpl.getService().StartService(VCUChassisFragment::class.java.toString())
        }
    }

    override fun onStop() {
        if (STCarApplication.inUIDebugMode) {
            MockMessageServiceImpl.getService().StopService(VCUChassisFragment::class.java.toString())
        }
        chassisViewModel?.lockControls()
        super.onStop()
    }

    override fun onDestroyView() {
        navigationLayoutListener?.let { view?.viewTreeObserver?.removeOnGlobalLayoutListener(it) }
        navigationLayoutListener = null
        chassisViewModel?.onPageExited()
        chassisViewModel = null
        super.onDestroyView()
    }
}
