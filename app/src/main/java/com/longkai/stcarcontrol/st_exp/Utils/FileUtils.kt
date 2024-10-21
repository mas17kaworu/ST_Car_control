package com.longkai.stcarcontrol.st_exp.Utils

import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset


fun writeToExternalStorage(esrFsaList: List<String>) {
    // Ensure external storage is available
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        // Get the external storage directory
        val sdCardDir = Environment.getExternalStorageDirectory()

        // Create the "ai/esr" folder path
        val cnTargetDir = File(sdCardDir, "ai/esr/cn_fsa")
        val enTargetDir = File(sdCardDir, "ai/esr/en_fsa")

        // Create the directory if it doesn't exist
        if (!cnTargetDir.exists()) {
            cnTargetDir.mkdirs()  // Create the directory structure
        }
        if (!enTargetDir.exists()) {
            enTargetDir.mkdirs()  // Create the directory structure
        }

        // Define file paths for the two text files inside the "ai/esr" folder
        val file1 = File(cnTargetDir, "App.txt")
        val file2 = File(enTargetDir, "App.txt")

        // Write the first string to file1.txt
        writeTextToFile(file1, esrFsaList[0])

        // Write the second string to file2.txt
        writeTextToFile(file2, esrFsaList[1])
    } else {
        println("External storage is not available.")
    }
}

fun writeTextToFile(file: File, text: String) {
    try {
        // Use OutputStreamWriter to specify GBK encoding
        val fos = FileOutputStream(file)
        val osw = OutputStreamWriter(fos, Charset.forName("GBK"))

        // Write the content to the file
        osw.write(text)
        osw.flush()
        osw.close()

        println("File written successfully: ${file.absolutePath}")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}