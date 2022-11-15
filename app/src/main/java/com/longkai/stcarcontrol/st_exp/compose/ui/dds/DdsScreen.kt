package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.longkai.stcarcontrol.st_exp.Utils.recomposeHighlighter
import com.longkai.stcarcontrol.st_exp.compose.data.dds.test.ScreenLog
import com.longkai.stcarcontrol.st_exp.compose.ui.components.HeaderText
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.Route.INVALID_SERVICE_ID
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.Route.PARAM_SERVICE_ID
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.Route.ROUTE_CREATE_SERVICE
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.Route.ROUTE_EXPRESS_SERVICES
import com.longkai.stcarcontrol.st_exp.compose.ui.dds.Route.ROUTE_SERVICE_DETAILS
import kotlinx.coroutines.launch

private const val showScreenLog = false;

@Composable
fun DdsScreen(
    ddsViewModel: DdsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color.Transparent
    ) { paddingValues ->
        DdsNavHost(
            ddsViewModel = ddsViewModel,
            modifier = Modifier.padding(paddingValues)
        ) { message, duration ->
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(message, null, duration)
            }
        }
    }

    if (showScreenLog) {
        val logs by ScreenLog.logs.collectAsState(initial = emptyList())
        LazyColumn(
            modifier = Modifier
                .wrapContentWidth()
                .recomposeHighlighter(),
        ) {
            itemsIndexed(
                items = logs,
                key = { index, _ -> index}
            ) { _, log ->
                HeaderText(
                    modifier = Modifier
                        .width(500.dp)
                        .recomposeHighlighter(),
                    text = log
                )
            }
        }
    }
}

@Composable
fun DdsNavHost(
    ddsViewModel: DdsViewModel,
    modifier: Modifier = Modifier,
    showSnackbar: (String, SnackbarDuration) -> Unit
) {
    fun showSnackbarShort(message: String) = showSnackbar(message, SnackbarDuration.Short)

    val navController = rememberNavController()

    Box(modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = ROUTE_EXPRESS_SERVICES) {
            composable(ROUTE_EXPRESS_SERVICES) {
                ExpressServicesScreen(
                    ddsViewModel = ddsViewModel,
                    onCreateService = {
                        navController.navigate(ROUTE_CREATE_SERVICE)
                    },
                    onViewServiceDetails = { serviceName ->
                        navController.navigate("$ROUTE_SERVICE_DETAILS/$serviceName")
                    },
                    showSnackbar = {
                        showSnackbarShort(it)
                    }
                )
            }
            composable(ROUTE_CREATE_SERVICE) {
                ServiceDetailsScreen(
                    ddsViewModel = ddsViewModel,
                    serviceId = null,
                    onBack = {
                        navController.popBackStack()
                    },
                    showSnackbar = {
                        showSnackbarShort(it)
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
                    },
                    showSnackbar = {
                        showSnackbarShort(it)
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
