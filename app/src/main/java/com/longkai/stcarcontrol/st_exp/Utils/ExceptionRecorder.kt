package com.longkai.stcarcontrol.st_exp.Utils

import android.content.Context
import android.os.Looper
import android.os.SystemClock
import android.widget.Toast
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class CustomExceptionHandler(private val context: Context) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, ex: Throwable) {
        ex.printStackTrace()

        try {
            // 使用Toast来显示异常信息
            object : Thread() {
                override fun run() {
                    Looper.prepare()
                    Toast.makeText(
                        context, "很抱歉,程序出现异常,即将重启.",
                        Toast.LENGTH_LONG
                    ).show()
                    Looper.loop()
                }
            }.start()

            // 保存日志文件
            saveExceptionLogsToFile(ex)
            SystemClock.sleep(3000)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


        // Optionally, you can also re-throw the exception to terminate the application
        // defaultHandler.uncaughtException(t, e)
    }

    private fun saveExceptionLogsToFile(throwable: Throwable) {
        try {
            val sDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            val date = sDateFormat.format(Date())

            // Get the file path where the logs will be saved
            val logFilePath = context.getExternalFilesDir(null)?.absolutePath + File.separator + "${date}-exception_logs.txt"

            // Create a new file with the given file path
            val logFile = File(logFilePath)

            // If the file doesn't exist, create it
            if (!logFile.exists()) {
                logFile.createNewFile()
            }

            // Initialize a FileWriter to write to the file
            val writer = FileWriter(logFile, true)

            // Write the exception logs to the file
            writer.write("Exception occurred at ${Date().toString()}:\n")
            throwable.printStackTrace(PrintWriter(writer))
            writer.write("\n")

            // Close the FileWriter
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}