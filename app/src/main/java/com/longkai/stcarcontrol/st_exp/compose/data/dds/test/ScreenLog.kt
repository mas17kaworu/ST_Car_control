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
  private val _logs = MutableSharedFlow<List<String>>(extraBufferCapacity = 1)
  val logs: Flow<List<String>> = _logs.asSharedFlow()
  private val logHistory = mutableListOf<String>()

  fun log(log: String) {
    log("", log)
  }

  fun log(tag: String, log: String) {
    val newList = mutableListOf<String>()
    logHistory.add("$tag $log")
    newList.addAll(logHistory)
    if (logHistory.size > 15) logHistory.removeFirst()
    _logs.tryEmit(newList)
  }
}