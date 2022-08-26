package com.longkai.stcarcontrol.st_exp.compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.compose.Route.INVALID_SERVICE_ID
import com.longkai.stcarcontrol.st_exp.compose.Route.PARAM_SERVICE_ID
import com.longkai.stcarcontrol.st_exp.compose.Route.ROUTE_CREATE_SERVICE
import com.longkai.stcarcontrol.st_exp.compose.Route.ROUTE_EXPRESS_SERVICES
import com.longkai.stcarcontrol.st_exp.compose.Route.ROUTE_SERVICE_DETAILS
import com.longkai.stcarcontrol.st_exp.compose.data.AppContainer
import com.longkai.stcarcontrol.st_exp.compose.data.dds.notification.DigitalKeyUnlockService
import com.longkai.stcarcontrol.st_exp.compose.ui.components.ActivityViewContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.DdsViewModel
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.ExpressServicesScreen
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.ServiceDetailsScreen
import kotlinx.coroutines.launch

class DdsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appContainer = (application as STCarApplication).appContainer
        setContent {
            STCarTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    ActivityViewContainer() {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            DdsNavHost(appContainer = appContainer)
                        }
                    }
                }
            }
        }

        startService(Intent(this, DigitalKeyUnlockService::class.java))
    }

    @Composable
    fun DdsNavHost(appContainer: AppContainer) {
        val ddsViewModel: DdsViewModel = viewModel(
            factory = DdsViewModel.provideFactory(appContainer.ddsRepo)
        )
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = ROUTE_EXPRESS_SERVICES) {
            composable(ROUTE_EXPRESS_SERVICES) {
                ExpressServicesScreen(
                    ddsViewModel = ddsViewModel,
                    onCreateService = {
                        navController.navigate(ROUTE_CREATE_SERVICE)
                    },
                    onViewServiceDetails = { serviceName ->
                        navController.navigate("$ROUTE_SERVICE_DETAILS/$serviceName")
                    }
                )
            }
            composable(ROUTE_CREATE_SERVICE) {
                ServiceDetailsScreen(
                    ddsViewModel = ddsViewModel,
                    serviceId = null,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = "$ROUTE_SERVICE_DETAILS/{$PARAM_SERVICE_ID}",
                arguments = listOf(
                    navArgument(PARAM_SERVICE_ID) { type = NavType.LongType }
                )
            ) { backStackEntry ->
                ServiceDetailsScreen(
                    ddsViewModel = ddsViewModel,
                    serviceId = backStackEntry.arguments?.getLong(
                        PARAM_SERVICE_ID,
                        INVALID_SERVICE_ID
                    ),
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

object Route {
    const val ROUTE_EXPRESS_SERVICES = "expressServices"
    const val ROUTE_SERVICE_DETAILS = "serviceDetails"
    const val ROUTE_CREATE_SERVICE = "createService"

    const val PARAM_SERVICE_ID = "serviceId"
    const val INVALID_SERVICE_ID = -1L
}
