package com.longkai.stcarcontrol.st_exp.compose.data.dds.test

import com.longkai.stcarcontrol.st_exp.compose.data.Result
import com.longkai.stcarcontrol.st_exp.compose.data.Result.Loading
import com.longkai.stcarcontrol.st_exp.compose.data.dds.model.ServiceAction.OledAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

object ScreenLog {
  val _logs = MutableStateFlow<List<String>>(listOf("Logs...."))
  val logs: StateFlow<List<String>> = _logs

  private val logHistory = mutableListOf<String>()

  fun log(log: String) {
    log("", log)
  }

  fun log(tag: String, log: String) {
    logHistory.add("$tag $log")
    if (logHistory.size > 40) logHistory.removeFirst()
    _logs.update { logHistory }
  }
}