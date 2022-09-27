package com.longkai.stcarcontrol.st_exp.tracking

data class HistoryRecord(
    val recordName: String
)

data class HistoryRecordData(
    val recordName: String,
    val realPoints: List<TrackingData> = emptyList(),
    val pboxPoints: List<TrackingData> = emptyList()
)