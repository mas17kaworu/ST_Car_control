package com.longkai.stcarcontrol.st_exp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.dimensionResource
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.ChassisRoute
import com.longkai.stcarcontrol.st_exp.compose.ui.chassis.ChassisViewModel
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme

class VCUChassisFragment : Fragment() {
    private var chassisViewModel: ChassisViewModel? = null
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
        (view as ComposeView).setContent {
            STCarTheme {
                ProvideWindowInsets {
                    ChassisRoute(
                        viewModel = viewModel,
                        modifier = Modifier.statusBarsPadding().padding(
                            bottom = dimensionResource(R.dimen.chassis_vcu_navigation_inset)
                        )
                    )
                }
            }
        }
    }

    override fun onStop() {
        chassisViewModel?.lockControls()
        super.onStop()
    }

    override fun onDestroyView() {
        chassisViewModel?.onPageExited()
        chassisViewModel = null
        super.onDestroyView()
    }
}
